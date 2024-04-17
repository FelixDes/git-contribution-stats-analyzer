package cli

import cli.commands.FileMostFrequentPairsMetricsCommand
import cli.commands.GlobalMostFrequentPairsMetricsCommand
import cli.extensions.toCliString
import cli.extensions.toCliStringForUsers
import external.dto.FMFPairsMetricsFolderDto
import external.dto.GMFPairsMetricsResponseDto
import external.dto.GMFUserResponseDto
import external.service.FMFPairsMetricsProcessor
import external.service.GMFPairsMetricsProcessor
import gitparser.external.LocalGitParser
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import java.io.File

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val gitParser = LocalGitParser()
    val gmfpProcessor = GMFPairsMetricsProcessor()
    val fmfpProcessor = FMFPairsMetricsProcessor()

    val parser = ArgParser("git-usage-stats")

    val gmfpCommand = GlobalMostFrequentPairsMetricsCommand { url, withChanges, full ->
        val indexedDomainDto = gitParser.parse(File(url))
        when (val gmfpResponse = gmfpProcessor.process(indexedDomainDto)) {
            is GMFPairsMetricsResponseDto -> {
                print(
                    """
                    |Response:
                    |${gmfpResponse.pairs.joinToString("\n") { it.toCliString(true, withChanges, full) }}"""
                        .trimMargin("|")
                )
            }

            is GMFUserResponseDto -> {
                print(
                    "There is only one contributor in the repository: ${gmfpResponse.user.name}<mailto:${gmfpResponse.user.email.value}>\n" + if (withChanges) {
                        """
                    |
                    |Changed files: 
                    |${
                            gmfpResponse.editedFiles.joinToString("\n") {
                                if (full) it.toCliStringForUsers(
                                    setOf(gmfpResponse.user), true
                                ) else it.toCliString(true)
                            }
                        }""".trimMargin("|")
                    } else ""
                )
            }
        }
    }

    val fmfpCommand = FileMostFrequentPairsMetricsCommand { url, withChanges, full ->
        val indexedDomainDto = gitParser.parse(File(url))
        when (val fmfpResponse = fmfpProcessor.process(indexedDomainDto)) {
            is FMFPairsMetricsFolderDto -> {
                print(
                    """
                    |Response:
                    |${fmfpResponse.toCliString(false, withChanges, full)}"""
                        .trimMargin("|")
                )
            }
        }
    }
    parser.subcommands(
        gmfpCommand,
        fmfpCommand
    )
    parser.parse(args)
}
