package cli

import gitparser.external.LocalGitParser
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import java.io.File

fun main(args: Array<String>) {
    val parser = ArgParser("git-usage-stats")

    val url by parser.option(
        ArgType.String,
        shortName = "l",
        fullName = "link",
        description = "Link to git repository"
    ).required()
//    val dir by parser.option(
//        ArgType.String,
//        shortName = "d",
//        fullName = "destination",
//        description = "Where to load the repository - only for remote repositories. Default is the standard temp directory."
//    ).default(System.getProperty("java.io.tmpdir"))
//    val force by parser.option(
//        ArgType.Boolean,
//        shortName = "f",
//        fullName = "force",
//        description = ""
//    ).required()

    parser.parse(args)

    LocalGitParser.parse(File(url))
}
