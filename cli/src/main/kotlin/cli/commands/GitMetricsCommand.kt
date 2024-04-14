package cli.commands

import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand

@OptIn(ExperimentalCli::class)
abstract class GitMetricsCommand(name: String, help: String) : Subcommand(name, help) {
    protected val repositoryUrl by argument(
        ArgType.String,
        fullName = "link",
        description = "Link to git repository"
    )
}