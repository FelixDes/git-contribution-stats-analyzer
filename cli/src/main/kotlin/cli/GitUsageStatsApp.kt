package cli

import cli.commands.FileMostFrequentPairsMetricsCommand
import cli.commands.GlobalMostFrequentPairsMetricsCommand
import cli.extensions.toCliString
import external.dto.FMFPairsMetricsFolderDto
import external.dto.GMFPairsMetricsResponseDto
import external.service.FMFPairsMetricsProcessor
import external.service.GMFPairsMetricsProcessor
import gitparser.external.service.GitDomainProcessor
import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli

@OptIn(ExperimentalCli::class)
class GitUsageStatsApp(
    private val gitDomainProcessor: GitDomainProcessor = GitDomainProcessor(),
    private val gmfpProcessor: GMFPairsMetricsProcessor = GMFPairsMetricsProcessor(),
    private val fmfpProcessor: FMFPairsMetricsProcessor = FMFPairsMetricsProcessor(),
) {
    fun runCLI(args: Array<String>) {
        val parser = ArgParser("GitUsageStats")

        val gmfpCommand = GlobalMostFrequentPairsMetricsCommand { url, withChanges, full ->
            val indexedDomainDto = gitDomainProcessor.parse(url)
            println()
            when (val gmfpResponse = gmfpProcessor.process(indexedDomainDto)) {
                is GMFPairsMetricsResponseDto -> {
                    print(
                        """
                    |Response:
                    |${gmfpResponse.pairs.joinToString("\n") { it.toCliString(true, withChanges, full) }}"""
                            .trimMargin("|")
                    )
                }
            }
        }

        val fmfpCommand = FileMostFrequentPairsMetricsCommand { url, withChanges, full ->
            val indexedDomainDto = gitDomainProcessor.parse(url)
            println()
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
}