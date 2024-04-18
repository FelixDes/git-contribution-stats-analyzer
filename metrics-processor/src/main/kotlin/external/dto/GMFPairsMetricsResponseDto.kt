package external.dto

import external.DomainTreeFileDto

sealed interface GlobalMostFrequentPairsMetricsResponse

data class GMFPairsMetricsResponseDto(
    val pairs: Set<GMFPairMetricDto>
) : GlobalMostFrequentPairsMetricsResponse

data class GMFPairMetricDto(
    val pair: UserPair,
    val commonEditedFiles: Set<DomainTreeFileDto>
)