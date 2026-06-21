package me.eriknikli.rhenium.app

import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.common.AggregateException
import me.eriknikli.rhenium.common.RheniumException
import me.eriknikli.rhenium.common.runCommand
import me.eriknikli.rhenium.semanticAnalyzer.ISemanticAnalyzer
import me.eriknikli.rhenium.transpiler.ITranspiler
import org.antlr.v4.runtime.CharStreams
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.Path
import kotlin.io.path.outputStream

interface IRheniumCompiler {
    fun compile(options: CompilerOptions)
}

@Singleton
class RheniumCompiler
@Inject
constructor(
    private val astBuilder: IAstBuilder,
    private val semanticAnalyzer: ISemanticAnalyzer,
    private val transpiler: ITranspiler,
    private val logger: Logger
) : IRheniumCompiler {
    override fun compile(options: CompilerOptions) {
        try {
            val stream = CharStreams.fromFileName(options.inputPath)

            val ast = astBuilder.parse(stream)
            semanticAnalyzer.decorateSemanticContext(ast)

            val output = Path("${options.inputPath}.c").outputStream()
            output.use {
                transpiler.transpile(ast, it)
            }

            "clang ${options.inputPath}.c -o ${options.inputPath}.o -lm".runCommand()
            "./${options.inputPath}.o".runCommand()
        } catch (exception: Exception) {
            handleException(exception)
        }
    }

    private fun handleException(throwable: Throwable) {
        if (throwable !is Exception) {
            throw throwable
        }

        if (throwable is AggregateException) {
            for (child in throwable.children) {
                handleException(child)
            }
            return
        }

        if (throwable is RheniumException) {
            logger.error(throwable.message)
        }

        logger.error("Fatal error.", throwable)
    }
}
