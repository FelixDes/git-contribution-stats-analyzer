package external.service

import external.*
import external.dto.*
import kotlin.math.min

class GMFPairsMetricsProcessor {
    fun process(
        domain: IndexedDomainDto
    ): GlobalMostFrequentPairsMetricsResponse {
        val source: List<Pair<User, Set<Path>>> = domain.usersChangeFilesAndFolders
            .asSequence()
            .map {
                it.key to it.value
                    .asSequence()
                    .filter { it.isFile }
                    .toSet()
            }
            .filter { it.second.isNotEmpty() }
            .sortedByDescending { it.second.size }
            .toList()

        if (source.size <= 1) {
            throw NotEnoughUsersToComputePair()
        }

        val result = hashSetOf<GMFPairMetricDto>()
        var rightBound = source.size
        var i = 0
        while (i < rightBound) {
            var j = i + 1
            while (j < rightBound) {
                val first = source[i].first
                val second = source[j].first
                val intersection = source[i].second.intersect(source[j].second)

                var rightBoundShift = source.binarySearch(0, rightBound) { intersection.size - it.second.size }
                if (rightBoundShift < 0) {
                    rightBoundShift = -rightBoundShift - 1
                }

                rightBound = min(rightBoundShift, rightBound)

                val dto = GMFPairMetricDto(
                    UserPair(first, second),
                    intersection
                        .asSequence()
                        .map { domain.files[it] ?: throw FileNotFoundInIndexException(it) }
                        .toHashSet()
                )
                if (result.isEmpty() || result.last().commonEditedFiles.size == dto.commonEditedFiles.size) {
                    result.add(dto)
                } else if (result.last().commonEditedFiles.size < dto.commonEditedFiles.size) {
                    result.clear()
                    result.add(dto)
                }
                ++j
            }
            ++i
        }
        return GMFPairsMetricsResponseDto(result)
    }
}