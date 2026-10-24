package me.eriknikli.rhenium.app

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import me.eriknikli.rhenium.ast.IAstBuilder
import me.eriknikli.rhenium.ast.tree.EntryDeclaration
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
    fun compile(options: CompilerOptions): Compilation
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
    override fun compile(options: CompilerOptions): Compilation {
        val stream = CharStreams.fromFileName(options.inputPath)

        val ast = astBuilder.parse(stream).getOrElse { return Compilation(it, null) }
        val diagnostics = semanticAnalyzer.decorateSemanticContext(ast).leftOrNull() ?: emptyList()
        if (diagnostics.hasErrors()) {
            return Compilation(diagnostics, null)
        }

        val entry = selectEntry(ast.entries, options).getOrElse { return Compilation(diagnostics, null, it) }
        val actions = lowerer.lower(ast, entry)

        val output = Path(options.cPath).outputStream()
        output.use {
            transpiler.transpile(actions, it)
        }

        val clangExitCode =
            "clang ${options.cPath} -o ${options.binaryPath} -lm -Wno-parentheses-equality".runCommand()
        if (clangExitCode != 0) {
            return Compilation(diagnostics, null)
        }

        return Compilation(diagnostics, options.binaryPath)
    }

    private fun selectEntry(entries: List<EntryDeclaration>, options: CompilerOptions): Either<String, EntryDeclaration> {
        val path = options.inputPath
        val name = options.entryName

        return when {
            entries.isEmpty() -> "nothing to run: no entry in $path.".left()
            name != null -> entries.find { it.name == name }?.right() ?: "no entry named '$name' in $path.".left()
            entries.size == 1 -> entries.single().right()
            else -> "several entries in $path, choose one: ${entries.joinToString(", ") { it.name }}.".left()
        }
    }
}
