package gus.external.dto

import gus.external.DomainTreeFileDto

sealed interface GlobalMostFrequentPairsMetricsResponse

data class GMFPairsMetricsResponseDto(
    val pairs: Set<GMFPairMetricDto>
) : GlobalMostFrequentPairsMetricsResponse

data class GMFPairMetricDto(
    val pair: UserPair,
    val commonEditedFiles: Set<DomainTreeFileDto>
)