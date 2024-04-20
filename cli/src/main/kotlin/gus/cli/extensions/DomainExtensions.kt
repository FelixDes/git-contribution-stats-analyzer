package gus.cli.extensions

import gus.external.DomainTreeFileDto
import gus.external.FileChange
import gus.external.User
import gus.external.dto.*

// To YAML string converters

fun DomainTreeFileDto.toCliString(indent: Boolean = false) = """
    |- Path: ${this.path.value}
    |  Type: ${this.type}"""
    .trimMargin("|")
    .indent(indent)

fun DomainTreeFileDto.toCliStringForUsers(users: Set<User>, indent: Boolean = false): String {
    val changes = users.filter { this.changes.containsKey(it) }.flatMap { this.changes[it]!! }
    return if (changes.isNotEmpty()) {
        """
        |- Path: ${this.path.value}
        |  Type: ${this.type}
        |  User${if (users.size == 1) "" else "s"} commits:
        |${changes.joinToString("\n") { it.toCliString(true) }}"""
            .trimMargin("|")
    } else {
        this.toCliString(indent)
    }.indent(indent)
}

fun UserPairsWithWeight.toCliString(indent: Boolean = false): String =
    if (this.userPairs.size == 1) {
        """
            |- Weight: ${this.weight}
            |  User pair:
            |${this.userPairs.first().set.joinToString("\n") { it.toCliString(true) }}"""
    } else {
        """
            |- Weight: ${this.weight}
            |  User pairs:
            |${this.userPairs.joinToString("\n") { it.toCliString(true) }}"""
    }
        .trimMargin("|")
        .indent(indent)

fun UserPair.toCliString(indent: Boolean = false) = """
    |- User pair:
    |${this.set.joinToString("\n") { it.toCliString(true) }}"""
    .trimMargin("|")
    .indent(indent)

fun User.toCliString(indent: Boolean = false) = this.toCliString(if (indent) 1 else 0)

fun User.toCliString(indent: Int = 1) = """
    |- Name: ${this.name}
    |  Email: mailto:${this.email.value}"""
    .trimMargin("|")
    .intIndent(indent)

fun FileChange.toCliString(indent: Boolean = false) = """
    |- Commit hash: ${this.commitName}
    |  Time: ${this.commitTimestamp}
    |  Commit message: >
    |${this.commitMessage.prependIndent("    ")}"""
    .trimMargin("|")
    .indent(indent)

fun GMFPairMetricDto.toCliString(
    indent: Boolean = false,
    withChanges: Boolean = false,
    full: Boolean = false
) = this.pair.toCliString().appendOnCondition(
    withChanges,
    """
        |- Changes:
        |${
        this.commonEditedFiles.joinToString("\n") {
            if (!full) it.toCliString(true) else it.toCliStringForUsers(
                this.pair.set,
                true
            )
        }
    }""")
    .trimMargin("|")
    .indent(indent)

fun FMFPairsMetricsFolderDto.toCliString(
    indent: Boolean = false,
    withChanges: Boolean = false,
    full: Boolean = false
): String = """
    |- Folder: ${this.path.value}"""
    .appendOnCondition(
        this.userPairWithCommitCount.userPairs.isNotEmpty(),
        """
                |  User pairs context:
                |${this.userPairWithCommitCount.toCliString(true)}"""
    )
    .appendOnCondition(
        this.subFolders.isNotEmpty(),
        """
                |  Inner folders:
                |${this.subFolders.joinToString("\n") { it.toCliString(true, withChanges, full) }}"""
    )
    .appendOnCondition(
        this.files.asSequence().filter { it.userPairWithCommitCount.userPairs.isNotEmpty() }.any(),
        """
                |  Inner files:
                |${
            this.files.asSequence().filter { it.userPairWithCommitCount.userPairs.isNotEmpty() }
                .joinToString("\n") { it.toCliString(true, withChanges, full) }
        }"""
    )
    .trimMargin("|")
    .indent(indent)

fun FMFPairsMetricsFileDto.toCliString(
    indent: Boolean = false,
    withChanges: Boolean = false,
    full: Boolean = false
) = """
    |- File: ${this.domainFile.path.value}"""
    .appendOnCondition(
        this.userPairWithCommitCount.userPairs.isNotEmpty(),
        """
        |  User pairs context:
        |${this.userPairWithCommitCount.toCliString(true)}"""
    )
    .appendOnCondition(
        withChanges,
        """
        |- File context:
        |${
            if (!full) this.domainFile.toCliString(true)
            else this.domainFile.toCliStringForUsers(
                this.userPairWithCommitCount.userPairs.asSequence().flatMap { it.set }.toSet(),
                true
            )
        }"""
    )
    .trimMargin("|")
    .indent(indent)

private fun String.appendOnCondition(condition: Boolean, str: String, `else`: String = "") =
    this + if (condition) str else `else`

private fun String.indent(isIndent: Boolean) = this.intIndent(if (isIndent) 1 else 0)
private fun String.intIndent(indent: Int = 1) = this.prependIndent("  ".repeat(indent))