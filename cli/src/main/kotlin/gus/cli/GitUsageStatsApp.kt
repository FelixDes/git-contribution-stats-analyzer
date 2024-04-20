package gus.cli

import gus.cli.commands.FileMostFrequentPairsMetricsCommand
import gus.cli.commands.GlobalMostFrequentPairsMetricsCommand
import gus.cli.extensions.toCliString
import gus.external.dto.FMFPairsMetricsFolderDto
import gus.external.dto.GMFPairsMetricsResponseDto
import gus.external.service.FMFPairsMetricsProcessor
import gus.external.service.GMFPairsMetricsProcessor
import gus.external.service.GitDomainProcessor
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
            fmfpCommand,
            gmfpCommand,
        )
        parser.parse(args)
    }
}