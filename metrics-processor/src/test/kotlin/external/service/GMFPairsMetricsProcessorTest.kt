package external.service

import external.*
import external.dto.GMFPairMetricDto
import external.dto.GMFPairsMetricsResponseDto
import external.dto.UserPair
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GMFPairsMetricsProcessorTest : FunSpec({
    val service = GMFPairsMetricsProcessor()

    test("computeGMFPairMetric") {
        val targetUser1 = User("u3", Email("u3@u.com"))
        val targetUser2 = User("u4", Email("u4@u.com"))
        val targetUser3 = User("u2", Email("u2@u.com"))

        val intersection12 = hashSetOf(
            Path("/a", true),
            Path("/d", true), Path("/e", true), Path("/f", true), Path("/g", true),
        )
        val intersection23 = hashSetOf(
            Path("/a", true), Path("/b", true),
            Path("/i", true), Path("/j", true), Path("/k", true)
        )
        val files = mapOf(
            Path("/a", true) to mockk<DomainTreeFileDto>(name = "a"),
            Path("/b", true) to mockk<DomainTreeFileDto>(name = "b"),
            Path("/c", true) to mockk<DomainTreeFileDto>(name = "c"),
            Path("/d", true) to mockk<DomainTreeFileDto>(name = "d"),
            Path("/e", true) to mockk<DomainTreeFileDto>(name = "e"),
            Path("/f", true) to mockk<DomainTreeFileDto>(name = "f"),
            Path("/g", true) to mockk<DomainTreeFileDto>(name = "g"),
            Path("/h", true) to mockk<DomainTreeFileDto>(name = "h"),
            Path("/i", true) to mockk<DomainTreeFileDto>(name = "i"),
            Path("/j", true) to mockk<DomainTreeFileDto>(name = "j"),
            Path("/k", true) to mockk<DomainTreeFileDto>(name = "k"),
        )

        val root = mockk<DomainTreeFolderDto>()
        every { root.cumulativeUsersFilesChanges } returns hashMapOf(
            User("u1", Email("u1@u.com")) to hashSetOf(
                Path("/a", true), Path("/b", true), Path("/c", true)
            ),
            targetUser1 to hashSetOf(
                Path("/a", true),
                Path("/d", true), Path("/e", true), Path("/f", true), Path("/g", true),
                Path("/h", true)
            ),
            targetUser2 to hashSetOf(
                Path("/a", true), Path("/b", true),
                Path("/d", true), Path("/e", true), Path("/f", true), Path("/g", true),
                Path("/i", true), Path("/j", true), Path("/k", true)
            ),
            targetUser3 to hashSetOf(
                Path("/a", true), Path("/b", true), Path("/c", true),
                Path("/h", true),
                Path("/i", true), Path("/j", true), Path("/k", true)
            ),
        )

        service.process(
            IndexedDomainDto(
                repositoryTree = root,
                users = mockk<Map<Email, User>>(),
                files = files,
                folders = mockk<Map<Path, DomainTreeFolderDto>>(),
                usersChangeFilesAndFolders = hashMapOf(
                    User("u1", Email("u1@u.com")) to hashSetOf(
                        Path("/a", true), Path("/b", true), Path("/c", true)
                    ),
                    targetUser1 to hashSetOf(
                        Path("/a", true),
                        Path("/d", true), Path("/e", true), Path("/f", true), Path("/g", true),
                        Path("/h", true)
                    ),
                    targetUser2 to hashSetOf(
                        Path("/a", true), Path("/b", true),
                        Path("/d", true), Path("/e", true), Path("/f", true), Path("/g", true),
                        Path("/i", true), Path("/j", true), Path("/k", true)
                    ),
                    targetUser3 to hashSetOf(
                        Path("/a", true), Path("/b", true), Path("/c", true),
                        Path("/h", true),
                        Path("/i", true), Path("/j", true), Path("/k", true)
                    ),
                )
            )
        ) shouldBe GMFPairsMetricsResponseDto(
            hashSetOf(
                GMFPairMetricDto(
                    UserPair(targetUser1, targetUser2), intersection12.map { files[it]!! }.toHashSet(),
                ),
                GMFPairMetricDto(
                    UserPair(targetUser2, targetUser3), intersection23.map { files[it]!! }.toHashSet(),
                ),
            )
        )
    }

    test("computeGMFPairMetricForOneUser") {
        val targetUser = User("u3", Email("u3@u.com"))

        val files = mapOf(
            Path("/a", true) to mockk<DomainTreeFileDto>(name = "a"),
            Path("/b", true) to mockk<DomainTreeFileDto>(name = "b"),
        )

        val root = mockk<DomainTreeFolderDto>()
        every { root.cumulativeUsersFilesChanges } returns hashMapOf(
            targetUser to hashSetOf(
                Path("/a", true),
                Path("/b", true)
            )
        )

        shouldThrow<NotEnoughUsersToComputePair> {
            service.process(
                IndexedDomainDto(
                    repositoryTree = root,
                    users = mockk<Map<Email, User>>(),
                    files = files,
                    folders = mockk<Map<Path, DomainTreeFolderDto>>(),
                    usersChangeFilesAndFolders = hashMapOf(
                        targetUser to hashSetOf(
                            Path("/a", true),
                            Path("/b", true)
                        )
                    )
                )
            )
        }
    }
})
