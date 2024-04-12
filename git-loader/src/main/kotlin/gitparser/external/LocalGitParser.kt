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


data object LocalGitParser {
    private class DiffCollector : RenameCallback() {
        private val diffs: MutableList<DiffEntry> = mutableListOf()

        override fun renamed(diff: DiffEntry) {
            diffs.add(diff)
        }
    }

    fun parse(dir: File): IndexedDomainDto {
        val allUsers = mutableMapOf<String, User>()

        val repo = FileRepository(dir)
        val topCommit = RevWalk(repo).parseCommit(repo.resolve(Constants.HEAD))

        val treeWalk = TreeWalk(repo)
        treeWalk.addTree(topCommit.tree)

        val config: Config = repo.config
        config.setBoolean("diff", null, "renames", true)
        val diffConfig = config.get(DiffConfig.KEY)

        val tree = treeWalkToTree(treeWalk) {
            val changes = mutableMapOf<User, MutableList<FileChange>>()

            val revWalk = RevWalk(repo)
            val diffCollector = DiffCollector()

            val ff = FollowFilter.create(it, diffConfig)
            ff.renameCallback = diffCollector
            revWalk.treeFilter = ff
            revWalk.markStart(revWalk.parseCommit(repo.resolve(Constants.HEAD)))

            revWalk.use { walk ->
                for (commitRev in walk) {
                    val authors = getAuthors(commitRev)
                    authors.forEach {
                        allUsers.putIfAbsent(it.email, it)
                    }

                    val fileChange = FileChange(
                        commitMessage = commitRev.fullMessage,
                        name = commitRev.name,
                        timestamp = ZonedDateTime.ofInstant(
                            commitRev.authorIdent.`when`.toInstant(),
                            commitRev.authorIdent.zoneId
                        ),
                    )
                    authors.forEach {
                        changes[it] = changes
                            .getOrDefault(it, mutableListOf())
                            .apply { add(fileChange) }
                    }
                }
            }

            changes.mapValues { it.value.toList() }

            return@treeWalkToTree changes.toMap()
        }
        return IndexedDomainDto(
            tree,
            allUsers.toMap()
        )
    }

    private fun treeWalkToTree(
        treeWalk: TreeWalk,
        findChanges: (path: String) -> Map<User, List<FileChange>>
    ): DomainTreeFolderDto {
        val root = MutableDomainTreeFolderDto("")
        val stack = ArrayDeque<MutableDomainTreeFolderDto>()
        stack.addLast(root)

        while (treeWalk.next()) {
            val currentPath = treeWalk.pathString

            if (treeWalk.isSubtree) {
                val folder = MutableDomainTreeFolderDto(currentPath)

                fitStack(stack, currentPath)

                stack.last().subFolders.add(folder)
                stack.addLast(folder)

                treeWalk.enterSubtree()
                continue
            } else {
                val filetype = FileTypeResolver.resolve(currentPath)
                val changes = findChanges(currentPath)
                val file = DomainTreeFileDto(currentPath, changes, filetype)
                fitStack(stack, currentPath)
                stack.last().files.add(file)
            }
        }

        return root.toDomainTreeFolderDto()
    }

    private fun fitStack(stack: ArrayDeque<MutableDomainTreeFolderDto>, currentPath: String) {
        while (stack.size > 1) {
            if (currentPath.slice(0..<currentPath.indexOfLast { it == '/' }) != stack.last().path) {
                stack.removeLast()
            } else break
        }
    }

    private fun getAuthors(commit: RevCommit): Set<User> {
        val result = mutableSetOf<User>()
        val msg = commit.fullMessage
        for (line in msg.split("\n")) {
            if (line.startsWith("Co-authored-by: ")) {
                val split = line.trim().split(" ")
                val name = split[split.size - 2]
                val email = split.last().removePrefix("<").removeSuffix(">")
                result.add(User(name, email))
            }
        }
        result.add(
            User(commit.authorIdent.name, commit.authorIdent.emailAddress)
        )

        return result
    }
}



