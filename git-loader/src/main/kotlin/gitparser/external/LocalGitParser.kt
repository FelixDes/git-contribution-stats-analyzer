package gitparser.external

import external.*
import gitparser.internal.dto.MutableDomainTreeFolderDto
import gitparser.internal.service.FileTypeResolver
import org.eclipse.jgit.diff.DiffConfig
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Config
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.FollowFilter
import org.eclipse.jgit.revwalk.RenameCallback
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.File
import java.time.ZonedDateTime


class LocalGitParser {
    private class DiffCollector : RenameCallback() {
        private val diffs: MutableList<DiffEntry> = mutableListOf()

        override fun renamed(diff: DiffEntry) {
            diffs.add(diff)
        }
    }

    fun parse(dir: File): IndexedDomainDto {
        val allUsers = mutableMapOf<Email, User>()
        val allFiles = mutableMapOf<Path, DomainTreeFileDto>()
        val allFolders = mutableMapOf<Path, DomainTreeFolderDto>()
        val usersChangeFiles = mutableMapOf<User, MutableSet<Path>>()

        val repo = FileRepository(dir)
        val topCommit = RevWalk(repo).parseCommit(repo.resolve(Constants.HEAD))

        val treeWalk = TreeWalk(repo)
        treeWalk.addTree(topCommit.tree)

        val config: Config = repo.config
        config.setBoolean("diff", null, "renames", true)
        val diffConfig = config.get(DiffConfig.KEY)

        val tree = treeWalkToTree(
            treeWalk,
            // Update indexes
            processFile = { file: DomainTreeFileDto ->
                allFiles.putIfAbsent(file.path, file)
                file.changes.forEach {
                    allUsers.putIfAbsent(it.key.email, it.key)
                    usersChangeFiles[it.key] = usersChangeFiles
                        .getOrDefault(it.key, hashSetOf())
                        .apply { add(file.path) }
                }
            },
            processFolder = { folder: DomainTreeFolderDto ->
                allFolders.putIfAbsent(folder.path, folder)
                folder.cumulativeUsersFilesChanges.forEach {
                    usersChangeFiles[it.key] = usersChangeFiles
                        .getOrDefault(it.key, hashSetOf())
                        .apply { add(folder.path) }
                }
            },
        ) {
            val changes = mutableMapOf<User, MutableSet<FileChange>>()

            val revWalk = RevWalk(repo)
            val diffCollector = DiffCollector()

            val ff = FollowFilter.create(it, diffConfig)
            ff.renameCallback = diffCollector
            revWalk.treeFilter = ff
            revWalk.markStart(revWalk.parseCommit(repo.resolve(Constants.HEAD)))

            revWalk.use { walk ->
                for (commitRev in walk) {
                    val authors = getAuthors(commitRev)

                    val fileChange = FileChange(
                        commitName = commitRev.name,
                        commitMessage = commitRev.fullMessage,
                        commitTimestamp = ZonedDateTime.ofInstant(
                            commitRev.authorIdent.`when`.toInstant(),
                            commitRev.authorIdent.zoneId
                        ),
                    )
                    authors.forEach { author ->
                        changes[author] = changes
                            .getOrDefault(author, hashSetOf())
                            .apply { add(fileChange) }
                    }
                }
            }

            changes.mapValues { it.value.toList() }

            return@treeWalkToTree changes.toMap()
        }
        return IndexedDomainDto(
            tree,
            allUsers.toMap(),
            allFiles,
            allFolders,
            usersChangeFiles.mapValues { it.value.toSet() }.toMap(),
        )
    }

    private fun treeWalkToTree(
        treeWalk: TreeWalk,
        processFile: (file: DomainTreeFileDto) -> Unit,
        processFolder: (folder: DomainTreeFolderDto) -> Unit,
        findChanges: (path: String) -> Map<User, Set<FileChange>>
    ): DomainTreeFolderDto {
        val root = MutableDomainTreeFolderDto(Path("", isFile = false))
        val stack = ArrayDeque<MutableDomainTreeFolderDto>()
        stack.addLast(root)

        while (treeWalk.next()) {
            val currentPath = treeWalk.pathString

            if (treeWalk.isSubtree) {
                val path = Path(currentPath, isFile = false)
                val folder = MutableDomainTreeFolderDto(path)

                fitStack(stack, currentPath)

                stack.last().subFolders.add(folder)
                stack.addLast(folder)

                treeWalk.enterSubtree()
                continue
            } else {
                val path = Path(currentPath, isFile = true)
                val filetype = FileTypeResolver.resolve(currentPath)
                val changes = findChanges(currentPath)
                val file = DomainTreeFileDto(path, changes, filetype)
                processFile(file)
                fitStack(stack, currentPath)
                stack.last().files.add(file)
            }
        }

        enrichCumulativeUsersChangeFiles(root)

        return root.toDomainTreeFolderDto(processFolder)
    }

    // Fit stack for a file path
    private fun fitStack(stack: ArrayDeque<MutableDomainTreeFolderDto>, currentPath: String) {
        while (stack.size > 1) {
            if (currentPath.slice(0..<currentPath.indexOfLast { it == '/' }) != stack.last().path.value) {
                stack.removeLast()
            } else break
        }
    }

    // Find all users that changed files
    private fun getAuthors(commit: RevCommit): Set<User> {
        val result = mutableSetOf<User>()
        val msg = commit.fullMessage
        for (line in msg.split("\n")) {
            if (line.startsWith("Co-authored-by: ")) {
                val split = line.trim().split(" ")
                val name = split[split.size - 2]
                val email = split.last().removePrefix("<").removeSuffix(">")
                result.add(User(name, Email(email)))
            }
        }
        result.add(
            User(commit.authorIdent.name, Email(commit.authorIdent.emailAddress))
        )

        return result
    }

    // Enrich MutableDomainTreeFolderDto with cumulative changes for folders
    private fun enrichCumulativeUsersChangeFiles(root: MutableDomainTreeFolderDto): MutableDomainTreeFolderDto {
        // Enrich every folder
        root.subFolders.forEach(::enrichCumulativeUsersChangeFiles)

        val target = root.cumulativeUsersChangeFiles

        // Process folder's files
        root.files.forEach { domainTreeFileDto ->
            domainTreeFileDto.changes.forEach { change ->
                val key = change.key
                val existingPlusCurrentFile: MutableSet<Path> = target.getOrDefault(
                    key, hashSetOf()
                ).toHashSet().apply { add(domainTreeFileDto.path) }

                target[key] = existingPlusCurrentFile
            }
        }

        // Process subfolder
        root.subFolders.forEach {
            it.cumulativeUsersChangeFiles.forEach { (key, value) ->
                target.merge(key, value) { old, new ->
                    old union new
                }
            }
        }
        return root
    }
}
