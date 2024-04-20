package gus.external.dto

import gus.external.DomainTreeFileDto

sealed interface GloballyMostFrequentPairsMetricsResponse

data class GMFPairsMetricsResponseDto(
    val pairs: Set<GMFPairMetricDto>
) : GloballyMostFrequentPairsMetricsResponse

data class GMFPairMetricDto(
    val pair: UserPair,
    val commonEditedFiles: Set<DomainTreeFileDto>
)