package gitparser.internal.dto

import external.*

data class MutableDomainTreeFolderDto(
    val path: Path,
    val subFolders: MutableSet<MutableDomainTreeFolderDto> = mutableSetOf(),
    val files: MutableSet<DomainTreeFileDto> = mutableSetOf(),
    val cumulativeUsersChangeFiles: MutableMap<User, Set<Path>> = mutableMapOf(),
) {
    fun toDomainTreeFolderDto(
        processFolder: (folder: DomainTreeFolderDto) -> Unit = {},
    ): DomainTreeFolderDto {
        val folder = DomainTreeFolderDto(
            this.path,
            this.subFolders.asSequence().map { it.toDomainTreeFolderDto(processFolder) }.toSet(),
            this.files.toSet(),
            this.cumulativeUsersChangeFiles.toMap()
        )
        processFolder(folder)
        return folder
    }
}