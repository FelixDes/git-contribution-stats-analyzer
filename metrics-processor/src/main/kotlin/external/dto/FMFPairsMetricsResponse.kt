package external.dto

import external.DomainTreeFileDto
import external.Path

sealed interface FileMostFrequentPairsMetricsResponse

data class FMFPairsMetricsFolderDto(
        val userPairWithCommitCount: UserPairsWithWeight,

        val path: Path,
        val subFolders: Set<FMFPairsMetricsFolderDto>,
        val files: Set<FMFPairsMetricsFileDto>,
) : FileMostFrequentPairsMetricsResponse

data class FMFPairsMetricsFileDto(
        val userPairWithCommitCount: UserPairsWithWeight,

        val domainFile: DomainTreeFileDto,
)

interface UserPairsWithWeight {
    val userPairs: Set<UserPair> // may be similar
    val weight: Int // sum of changes for 2 users
}

data class ImmutableUserPairsWithWeight(
    override val userPairs: Set<UserPair>,
    override val weight: Int,
) : UserPairsWithWeight

data class MutableUserPairsWithWeight(
    override val userPairs: MutableSet<UserPair>,
    override val weight: Int,
) : UserPairsWithWeight