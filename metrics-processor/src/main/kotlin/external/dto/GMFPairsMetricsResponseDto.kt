package external.dto

import external.DomainTreeFileDto
import external.User

sealed interface GlobalMostFrequentPairsMetricsResponse

data class GMFPairsMetricsResponseDto(
    val pairs: Set<GMFPairMetricDto>
) : GlobalMostFrequentPairsMetricsResponse

data class GMFUserResponseDto(
    val user: User,
    val editedFiles: Set<DomainTreeFileDto>,
) : GlobalMostFrequentPairsMetricsResponse

data class GMFPairMetricDto(
    val pair: UserPair,
    val commonEditedFiles: Set<DomainTreeFileDto>
)