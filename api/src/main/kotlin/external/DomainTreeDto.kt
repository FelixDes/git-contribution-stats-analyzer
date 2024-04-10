package external

import java.time.ZonedDateTime

data class DomainTreeDto(
    val path: String,
    val changes: Map<User, Commit>,
)

enum class FileType {
    SOURCE, TEST, CONFIG, UNKNOWN
}

data class User(
    val name: String,
    val email: String,
)

data class Commit(
    val timestamp: ZonedDateTime,
    val message: String,
    val author: User,
    val commitRef: String,
)

data class DomainFileDto(
    val path: String,
    val changes: List<Commit>,
    val type: FileType = FileType.UNKNOWN
)