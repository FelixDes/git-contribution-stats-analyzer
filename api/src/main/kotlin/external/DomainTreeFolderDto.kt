package external

import java.time.ZonedDateTime

data class Path(val value: String, val isFile: Boolean)

@JvmInline
value class Email(val value: String)

data class IndexedDomainDto(
    val repositoryTree: DomainTreeFolderDto,
    val users: Map<Email, User>,
    val files: Map<Path, DomainTreeFileDto>,
    val folders: Map<Path, DomainTreeFolderDto>,
    val usersChangeFilesAndFolders: Map<User, Set<Path>>,
)

data class DomainTreeFolderDto(
    val path: Path,
    val subFolders: Set<DomainTreeFolderDto>,
    val files: Set<DomainTreeFileDto>,
    val cumulativeUsersFilesChanges: Map<User, Set<Path>>,
)

enum class FileType {
    SOURCE, TEST, CONFIG, UNKNOWN
}

data class User(
    val name: String,
    val email: Email,
)

data class FileChange(
    val commitName: String,
    val commitMessage: String,
    val commitTimestamp: ZonedDateTime,
)

data class DomainTreeFileDto(
    val path: Path,
    val changes: Map<User, Set<FileChange>>,
    val type: FileType = FileType.UNKNOWN
)