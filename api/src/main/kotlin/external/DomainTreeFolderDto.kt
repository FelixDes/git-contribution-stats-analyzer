package external

import java.time.ZonedDateTime

@JvmInline
value class Path(val path: String)

data class IndexedDomainDto(
    val repositoryTree: DomainTreeFolderDto,
    val users: Map<Email, User>,
    val files: Map<Path, DomainTreeFileDto>,
)

data class DomainTreeFolderDto(
    val path: Path,
    val subFolders: List<DomainTreeFolderDto>,
    val files: List<DomainTreeFileDto>,
)

enum class FileType {
    SOURCE, TEST, CONFIG, UNKNOWN
}

@JvmInline
value class Email(val email: String)


data class User(
    val name: String,
    val email: String,
)

data class FileChange(
    val timestamp: ZonedDateTime,
    val commitMessage: String,
    val commitName: String,
)

data class DomainTreeFileDto(
    val path: String,
    val changes: Map<User, List<FileChange>>,
    val type: FileType = FileType.UNKNOWN
)