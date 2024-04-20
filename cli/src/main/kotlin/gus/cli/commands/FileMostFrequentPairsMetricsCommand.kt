package gus.cli.commands

import kotlinx.cli.ArgType
import kotlinx.cli.default

class FileMostFrequentPairsMetricsCommand(private val process: (url: String, withChanges: Boolean, full: Boolean) -> Unit) :
    GitMetricsCommand(
        "fmfp",
        "Calculate the pairs of developers per files/folders who most frequently contribute to the same files/folders"
    ) {
    private var withChanges by option(
        ArgType.Boolean,
        shortName = "c",
        fullName = "changes",
        description = "Toggles file changes history output"
    ).default(false)
    private var full by option(
        ArgType.Boolean,
        shortName = "f",
        fullName = "full",
        description = "Toggles full output (applicable only if -c flag is active)"
    ).default(false)

    override fun execute() = process(repositoryUrl, withChanges, full)
}