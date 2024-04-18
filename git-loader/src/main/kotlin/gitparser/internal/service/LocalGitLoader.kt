package gitparser.internal.service

import gitparser.external.InvalidRepoLinkException
import mu.KotlinLogging
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Repository
import java.io.File

class LocalGitLoader {
    private val logger = KotlinLogging.logger {}

    fun load(dir: File): Repository {
        // Check if url is a valid git url
        val repoUrl = dir.path
        if (!repoUrl.endsWith(".git") || !dir.exists()) {
            throw InvalidRepoLinkException(repoUrl)
        }

        logger.info { "Using existing git repo at ${dir.absolutePath}" }
        return FileRepository(dir)
    }
}
