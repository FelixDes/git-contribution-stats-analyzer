package gitparser.internal.dto

import external.DomainTreeFileDto
import external.DomainTreeFolderDto

data class MutableDomainTreeFolderDto(
    val path: String,
    val subFolders: MutableList<MutableDomainTreeFolderDto> = mutableListOf(),
    val files: MutableList<DomainTreeFileDto> = mutableListOf(),
) {
    fun toDomainTreeFolderDto(): DomainTreeFolderDto {
        return DomainTreeFolderDto(
            this.path,
            this.subFolders.map { it.toDomainTreeFolderDto() }.toList(),
            this.files.toList(),
        )
    }
}