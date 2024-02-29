import com.cjsoftware.antlr4docgen.parser.DefaultStreamProvider
import com.cjsoftware.antlr4docgen.parser.GrammarModelBuilder
import com.cjsoftware.antlr4docgen.railroad.AntoraDiagram
import com.cjsoftware.antlr4docgen.railroad.HtmlDiagram
import com.cjsoftware.antlr4docgen.railroad.buildDiagram
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.path
import java.io.File
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.name

enum class GeneratorType {
    HTML,
    ANTORA,
}

class Generator : CliktCommand() {
    private val type: GeneratorType? by option(
        help = "Generator type",
    ).enum<GeneratorType>()
        .required()

    private val sourceGrammarFile: File by argument(
        help = "A source grammar file",
    ).file(mustExist = true, mustBeReadable = true)

    private val targetDirectory: Path by argument(
        help = "A target directory where the output will be placed",
    ).path(mustExist = true, mustBeWritable = true)

    override fun run() {
        val rules = buildDiagram(
            GrammarModelBuilder(
                DefaultStreamProvider(
                    sourceGrammarFile.parentFile,
                ),
            ).processGrammar(sourceGrammarFile),
        )

        val d = when (type) {
            GeneratorType.HTML -> HtmlDiagram()
            GeneratorType.ANTORA -> AntoraDiagram()
            else -> throw RuntimeException("Dead code reached in generator type detection")
        }

        d.render(targetDirectory, rules)
    }
}

fun main(args: Array<String>) = Generator().main(args)
