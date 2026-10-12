package me.eriknikli.rhenium.app

import arrow.core.getOrElse
import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.common.diagnostics.Diagnostic
import me.eriknikli.rhenium.common.diagnostics.hasErrors
import me.eriknikli.rhenium.common.runCommand
import me.eriknikli.rhenium.lowering.ILowerer
import me.eriknikli.rhenium.semanticAnalyzer.ISemanticAnalyzer
import me.eriknikli.rhenium.transpiler.ITranspiler
import org.antlr.v4.runtime.CharStreams
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.path.Path
import kotlin.io.path.outputStream

interface IRheniumCompiler {
    fun compile(options: CompilerOptions): List<Diagnostic>
}

@Singleton
class RheniumCompiler
@Inject
constructor(
    private val astBuilder: IAstBuilder,
    private val semanticAnalyzer: ISemanticAnalyzer,
    private val lowerer: ILowerer,
    private val transpiler: ITranspiler
) : IRheniumCompiler {
    override fun compile(options: CompilerOptions): List<Diagnostic> {
        val stream = CharStreams.fromFileName(options.inputPath)

        val ast = astBuilder.parse(stream).getOrElse { return it }
        val diagnostics = semanticAnalyzer.decorateSemanticContext(ast).fold({ it }, { emptyList() })
        if (diagnostics.hasErrors()) {
            return diagnostics
        }

        val actions = lowerer.lower(ast)

        val output = Path("${options.inputPath}.c").outputStream()
        output.use {
            transpiler.transpile(actions, it)
        }

        "clang ${options.inputPath}.c -o ${options.binaryPath} -lm -Wno-parentheses-equality".runCommand()

        return diagnostics
    }
}
