package gitparser.external.service

import external.IndexedDomainDto
import gitparser.internal.service.ExternalGitLoader
import gitparser.internal.service.GitRepoParser
import gitparser.internal.service.LocalGitLoader
import java.io.File
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL


class GitDomainProcessor(
    private val gitParser: GitRepoParser = GitRepoParser(),
    private val localGitLoader: LocalGitLoader = LocalGitLoader(),
    private val externalGitLoader: ExternalGitLoader = ExternalGitLoader(),
) {
    companion object {
        fun isValidURL(url: String): Boolean = try {
            URL(url).toURI();
            true
        } catch (e: MalformedURLException) {
            false
        } catch (e: URISyntaxException) {
            false
        }
    }

    fun parse(link: String): IndexedDomainDto {
        return gitParser.parse(
            if (isValidURL(link)) {
                externalGitLoader.load(URI.create(link).toURL())
            } else {
                localGitLoader.load(File(link))
            }
        )
    }
}