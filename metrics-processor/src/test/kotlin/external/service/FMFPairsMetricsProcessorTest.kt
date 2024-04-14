
package external.service

import external.*
import external.dto.FMFPairsMetricsFileDto
import external.dto.FMFPairsMetricsFolderDto
import external.dto.UserPair
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.mockk

class FMFPairsMetricsProcessorTest : FunSpec({
    val service = FMFPairsMetricsProcessor()

    fun setOfFileChanges(size: Int): Set<FileChange> {
        val res = mutableSetOf<FileChange>()
        for (i in 0 until size) {
            res.add(mockk<FileChange>())
        }
        return res
    }


    test("computeFMFPairMetric") {
        val userA = User("uA", Email("uA@u.com"))
        val userB = User("uB", Email("uB@u.com"))
        val userC = User("uC", Email("uC@u.com"))
        val userD = User("uD", Email("uD@u.com"))

        val users = mapOf(
            userA.email to userA,
            userB.email to userB,
            userC.email to userC,
            userD.email to userD,
        )

        val files = mapOf(
            Path("0/1/3/a", true) to DomainTreeFileDto(
                Path("0/1/3/a", true),
                mapOf(
                    userA to setOfFileChanges(2),
                    userB to setOfFileChanges(3),
                    userC to setOfFileChanges(2),
                    userD to setOfFileChanges(1),
                )
            ),
            Path("0/1/3/b", true) to DomainTreeFileDto(
                Path("0/1/3/b", true),
                mapOf(
                    userA to setOfFileChanges(2),
                    userB to setOfFileChanges(4),
                    userC to setOfFileChanges(1),
                    userD to setOfFileChanges(1),
                )
            ),
            Path("0/1/3/c", true) to DomainTreeFileDto(
                Path("0/1/3/c", true),
                mapOf(
                    userA to setOfFileChanges(3),
                    userB to setOfFileChanges(7),
                    userC to setOfFileChanges(1),
                    userD to setOfFileChanges(3),
                )
            ),
            Path("0/1/4/d", true) to DomainTreeFileDto(
                Path("0/1/4/d", true),
                mapOf(
                    userA to setOfFileChanges(7),
                    userB to setOfFileChanges(1),
                    userC to setOfFileChanges(1),
                    userD to setOfFileChanges(1),
                )
            ),
            Path("0/1/4/e", true) to DomainTreeFileDto(
                Path("0/1/4/e", true),
                mapOf(
                    userA to setOfFileChanges(5),
                    userB to setOfFileChanges(6),
                )
            ),
            Path("0/2/f", true) to DomainTreeFileDto(
                Path("0/2/f", true),
                mapOf(
                    userA to setOfFileChanges(10),
                )
            ),
            Path("0/2/g", true) to DomainTreeFileDto(
                Path("0/2/g", true),
                mapOf(
                    userA to setOfFileChanges(2),
                    userB to setOfFileChanges(2),
                    userC to setOfFileChanges(2),
                    userD to setOfFileChanges(2),
                )
            ),
        )

        val folder3Files: Set<Path> = setOf(
            Path("0/1/3/a", true),
            Path("0/1/3/b", true),
            Path("0/1/3/c", true)
        )
        val folder3 = DomainTreeFolderDto(
            Path("0/1/3", false),
            setOf(),
            folder3Files.asSequence().map { files[it]!! }.toSet(),
            mapOf(
                userA to folder3Files,
                userB to folder3Files,
                userC to folder3Files,
                userD to folder3Files,
            )
        )

        val folder4Files: Set<Path> = setOf(
            Path("0/1/4/d", true),
            Path("0/1/4/e", true)
        )
        val folder4 = DomainTreeFolderDto(
            Path("0/1/4", false),
            setOf(),
            folder4Files.asSequence().map { files[it]!! }.toSet(),
            mapOf(
                userA to folder4Files,
                userB to folder4Files,
                userC to setOf(Path("0/1/4/d", true)),
                userD to setOf(Path("0/1/4/d", true)),
            )
        )

        val folder2Files: Set<Path> = setOf(
            Path("0/2/f", true),
            Path("0/2/g", true)
        )
        val folder2 = DomainTreeFolderDto(
            Path("0/2", false),
            setOf(),
            folder2Files.asSequence().map { files[it]!! }.toSet(),
            mapOf(
                userA to folder2Files,
                userB to setOf(Path("0/2/g", true)),
                userC to setOf(Path("0/2/g", true)),
                userD to setOf(Path("0/2/g", true)),
            )
        )

        val folder1 = DomainTreeFolderDto(
            Path("0/1", false),
            setOf(folder3, folder4),
            setOf(),
            mapOf(
                userA to folder3Files + folder4Files,
                userB to folder3Files + folder4Files,
                userC to setOf(
                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true)
                ),
                userD to setOf(
                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true)
                )
            )
        )

        val root = DomainTreeFolderDto(
            Path("0", false),
            setOf(folder1, folder2),
            setOf(),
            mapOf(
                userA to files.keys,
                userB to setOf(
                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true),
                    Path("0/1/4/e", true),
                    Path("0/2/g", true)
                ),
                userC to setOf(
                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true),
                    Path("0/2/g", true)
                ),
                userD to setOf(
                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true),
                    Path("0/2/g", true)
                ),
            )
        )


        val folders = mapOf(
            Path("0", false) to root,
            Path("0/1", false) to folder1,
            Path("0/2", false) to folder2,
            Path("0/1/3", false) to folder3,
            Path("0/1/4", false) to folder4,
        )

        val indexedDomain = IndexedDomainDto(
            repositoryTree = root,
            users = users,
            files = files,
            folders = folders,
            usersChangeFilesAndFolders = mapOf(
                userA to files.keys + folders.keys,
                userB to setOf(
                    Path("0", false),
                    Path("0/1", false),
                    Path("0/2", false),
                    Path("0/1/3", false),
                    Path("0/1/4", false),

                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true),
                    Path("0/1/4/e", true),
                    Path("0/2/g", true)
                ),
                userC to setOf(
                    Path("0", false),
                    Path("0/1", false),
                    Path("0/2", false),
                    Path("0/1/3", false),
                    Path("0/1/4", false),

                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true),
                    Path("0/2/g", true)
                ),
                userD to setOf(
                    Path("0", false),
                    Path("0/1", false),
                    Path("0/2", false),
                    Path("0/1/3", false),
                    Path("0/1/4", false),

                    Path("0/1/3/a", true),
                    Path("0/1/3/b", true),
                    Path("0/1/3/c", true),
                    Path("0/1/4/d", true),
                    Path("0/2/g", true)
                ),
            )
        )

        val response = service.process(indexedDomain)
        response shouldBeSameInstanceAs (FMFPairsMetricsFileDto::class)
        response should {
            val dto = it as FMFPairsMetricsFolderDto
            dto.userPairWithCommitCount.userPairs.shouldBe(setOf(UserPair(userA, userB)))
            dto.userPairWithCommitCount.weight.shouldBe(54)
        }
    }
})
