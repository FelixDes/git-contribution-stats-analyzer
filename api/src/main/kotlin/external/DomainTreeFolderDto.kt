package external

import java.time.ZonedDateTime

data class IndexedDomainDto(
    val repositoryTree: DomainTreeFolderDto,
    val users: Map<String, User>,
)

data class DomainTreeFolderDto(
    val path: String,
    val subFolders: List<DomainTreeFolderDto>,
    val files: List<DomainTreeFileDto>,
)

enum class FileType {
    SOURCE, TEST, CONFIG, UNKNOWN
}

data class User(
    val name: String,
    val email: String,
)

data class FileChange(
    val timestamp: ZonedDateTime,
    val commitMessage: String,
    val name: String,
)

data class DomainTreeFileDto(
    val path: String,
    val changes: Map<User, List<FileChange>>,
    val type: FileType = FileType.UNKNOWN
)