package gus.internal.service

import gus.external.InvalidRepoLinkException
import gus.internal.APP_FOLDER_NAME
import mu.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Repository
import java.io.File
import java.net.URL

class ExternalGitLoader(
    private val repoNameExtractor: RepoNameExtractor = RepoNameExtractor()
) {
    private val logger = KotlinLogging.logger {}

    fun load(
        url: URL,
        tempDir: File = File(System.getProperty("java.io.tmpdir"))
    ): Repository {
        // Check if url is a valid git url
        val repoUrl = url.toExternalForm()
        if (!repoUrl.endsWith(".git")) {
            throw InvalidRepoLinkException(repoUrl)
        }

        // Create temp folder for app
        val appFolder = File(tempDir, APP_FOLDER_NAME)
        if (!appFolder.exists()) {
            appFolder.mkdir()
        }

        val repoName = repoNameExtractor.extract(repoUrl)

        val repoFolder = File(appFolder, repoName)

        val git = if (repoFolder.exists()) {
            if (File(repoFolder, ".git").exists()) {
                logger.info { "Using existing git repo '$repoName' at ${repoFolder.absolutePath} - [updating]" }
                val repo = Git.open(repoFolder)
                repo.reset().setMode(ResetCommand.ResetType.HARD).call()
                repo.pull().call()
                logger.info { "Updated git repo '$repoName" }
                repo
            } else {
                logger.info { "Cloning git repo '$repoName' to ${repoFolder.absolutePath} - [loading]" }
                val repo = Git.cloneRepository().setURI(repoUrl).setDirectory(repoFolder).call()
                logger.info { "Cloned git repo '$repoName" }
                repo
            }
        } else {
            repoFolder.mkdir()
            logger.info { "Cloning git repo '$repoName' to ${repoFolder.absolutePath} - [loading]" }
            val repo = Git.cloneRepository().setURI(repoUrl).setDirectory(repoFolder).call()
            logger.info { "Cloned git repo '$repoName" }
            repo
        }

        return git.repository
    }
}