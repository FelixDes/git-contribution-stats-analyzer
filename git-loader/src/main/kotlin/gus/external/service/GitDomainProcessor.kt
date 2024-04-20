package gus.external.service

import gus.external.IndexedDomainDto
import gus.internal.service.ExternalGitLoader
import gus.internal.service.GitRepoParser
import gus.internal.service.LocalGitLoader
import org.apache.commons.validator.routines.UrlValidator
import java.io.File
import java.net.URI

class GitDomainProcessor(
    private val gitParser: GitRepoParser = GitRepoParser(),
    private val localGitLoader: LocalGitLoader = LocalGitLoader(),
    private val externalGitLoader: ExternalGitLoader = ExternalGitLoader(),
) {
    fun parse(link: String): IndexedDomainDto {
        return gitParser.parse(
            if (UrlValidator.getInstance().isValid(link)) {
                externalGitLoader.load(URI.create(link).toURL())
            } else {
                localGitLoader.load(File(link))
            }
        )
    }
}