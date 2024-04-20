package gus.internal.service

class RepoNameExtractor {
    fun extract(repoUrl: String): String {
        return repoUrl
            .removeSuffix(".git")
            .dropLastWhile { it == '/' }
            .split('/')
            .last()
    }
}