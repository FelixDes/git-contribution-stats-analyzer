package gus.external.service

import gus.external.*
import gus.external.dto.*

class FMFPairsMetricsProcessor {
    fun process(
        domain: IndexedDomainDto
    ): FileMostFrequentPairsMetricsResponse {
        return buildFMFPairsMetricsFolderResponse(
            domain,
            getPathsWithContributors(domain)
        )
    }

    // Get all paths to sets (if many pairs have the same wight) of pairs
    private fun getPathsWithContributors(domain: IndexedDomainDto): Map<Path, UserPairsWithWeight> {
        if (domain.usersChangeFilesAndFolders.size <= 1) {
            throw NotEnoughUsersToComputePair()
        }

        val source: List<Pair<User, Set<Path>>> = domain.usersChangeFilesAndFolders.toList()
        val pathsWithUsers = mutableMapOf<Path, MutableUserPairsWithWeight>()

        for (i in source.indices) {
            for (j in i + 1 until source.size) {
                val first = source[i].first
                val second = source[j].first

                source[i].second.intersect(source[j].second).forEach {
                    val currentPair = UserPair(first, second)
                    val currentWeight = getFileWeightForUser(it, first, domain) + getFileWeightForUser(it, second, domain)

                    if (!pathsWithUsers.contains(it) || pathsWithUsers[it]!!.weight < currentWeight) {
                        pathsWithUsers[it] = MutableUserPairsWithWeight(
                            hashSetOf(currentPair),
                            currentWeight
                        )
                    } else if (pathsWithUsers[it]!!.weight == currentWeight) {
                        pathsWithUsers[it]!!.userPairs.add(currentPair)
                    }
                }
            }
        }

        // If all users have the same numbers of commits -> skip (no 'most frequent')
        val userCount = domain.users.size
        val filteredMap = pathsWithUsers
            .asSequence()
            .filter { it.value.userPairs.size != userCount * (userCount - 1) / 2 }
            .associateBy({ it.key }, { it.value })
        return filteredMap
    }

    private fun getFileWeightForUser(path: Path, user: User, domain: IndexedDomainDto): Int {
        return when {
            domain.files.contains(path) -> {
                domain.files[path]!!.changes.getOrElse(user) {
                    throw UserNotFoundInIndexException(user)
                }.size
            }

            domain.folders.contains(path) -> {
                domain.folders[path]!!.cumulativeUsersFilesChanges.getOrElse(user) {
                    throw UserNotFoundInIndexException(user)
                }.sumOf { domain.files[it]!!.changes[user]!!.size }
            }

            else -> throw FileNotFoundInIndexException(path)
        }
    }

    private fun buildFMFPairsMetricsFolderResponse(
        domain: IndexedDomainDto,
        pathsWithUsers: Map<Path, UserPairsWithWeight>
    ): FMFPairsMetricsFolderDto {
        return domain.repositoryTree.toFMFPairsMetricsFolderResponseDto(
            { file -> pathsWithUsers[file.path] ?: ImmutableUserPairsWithWeight(setOf(), 0) },
            { folder -> pathsWithUsers[folder.path] ?: ImmutableUserPairsWithWeight(setOf(), 0) },
        )
    }
}

private fun DomainTreeFolderDto.toFMFPairsMetricsFolderResponseDto(
    getMostFrequentPairForFile: (file: DomainTreeFileDto) -> UserPairsWithWeight,
    getMostFrequentPairForFolder: (folder: DomainTreeFolderDto) -> UserPairsWithWeight,
): FMFPairsMetricsFolderDto {
    val files = this.files.asSequence().map {
        FMFPairsMetricsFileDto(
            getMostFrequentPairForFile(it),
            it
        )
    }.toSet()

    return FMFPairsMetricsFolderDto(
        getMostFrequentPairForFolder(this),
        this.path,
        this.subFolders.asSequence().map {
            it.toFMFPairsMetricsFolderResponseDto(
                getMostFrequentPairForFile,
                getMostFrequentPairForFolder
            )
        }.toSet(),
        files,
    )
}