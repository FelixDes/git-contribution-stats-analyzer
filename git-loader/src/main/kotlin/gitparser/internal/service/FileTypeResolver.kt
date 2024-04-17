package gitparser.internal.service

import external.FileType

object FileTypeResolver {
    fun resolve(filePath: String): FileType {
        // TODO: should be implemented by some external lib/service
        return when {
            filePath.contains("test/") -> FileType.TEST
            filePath.contains("src/") -> FileType.SOURCE
            arrayOf(
                ".toml",
                ".yaml",
                ".yml",
                ".json",
                ".cfg",
                ".ini",
                ".conf",
                ".properties",
                ".xml",
                ".kts",
            ).any { filePath.endsWith(it) } -> FileType.CONFIG

            else -> FileType.UNKNOWN
        }
    }
}
