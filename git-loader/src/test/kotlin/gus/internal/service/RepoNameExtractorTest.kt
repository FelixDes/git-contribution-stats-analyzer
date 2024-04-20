package gus.internal.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RepoNameExtractorTest : FunSpec({
    val repoNameExtractor = RepoNameExtractor()

    test("extracts repo name from url with .git suffix") {
        val repoName = repoNameExtractor.extract("https://github.com/gus/example.git")
        repoName shouldBe "example"
    }

    test("extracts repo name from url with /.git suffix") {
        val repoName = repoNameExtractor.extract("https://github.com/gus/example/.git")
        repoName shouldBe "example"
    }
})