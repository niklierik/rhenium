package me.eriknikli.rhenium.semanticAnalyzer

import arrow.core.raise.either
import arrow.core.nel
import arrow.core.raise.ensure
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.zipOrAccumulate
import me.eriknikli.rhenium.ast.tree.EntryDeclaration
import me.eriknikli.rhenium.ast.tree.RootNode
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.declarations.IFunctionDeclarationDecorator
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.EntryAlreadyDeclared
import me.eriknikli.rhenium.semanticAnalyzer.statements.IStatementNodeDecorator
import me.eriknikli.rhenium.semanticAnalyzer.statements.StatementDecoratorContext
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.globalScope
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import javax.inject.Inject
import javax.inject.Singleton

interface IRootNodeDecorator {
    fun decorate(rootNode: RootNode): Diagnosed<Unit>
}

@Singleton
class RootNodeDecorator
@Inject
constructor() : IRootNodeDecorator {
    @Inject
    lateinit var statementDecorator: IStatementNodeDecorator

    @Inject
    lateinit var functionDecorator: IFunctionDeclarationDecorator

    override fun decorate(rootNode: RootNode): Diagnosed<Unit> = either {
        val scope = globalScope()

        val entries = rootNode.entries
        val functions = rootNode.functions

        val declared = either { mapOrAccumulate(functions) { functionDecorator.declare(it, scope).bindNel() } }

        zipOrAccumulate(
            { declared.bindNel() },
            { mapOrAccumulate(functions) { functionDecorator.decorateBody(it).bindNel() } },
            { decorateEntries(entries, scope).bindNel() }
        ) { _, _, _ -> }
    }

    private fun decorateEntries(entries: List<EntryDeclaration>, scope: Scope): Diagnosed<Unit> = either {
        mapOrAccumulate(entries) { entry ->
            either {
                zipOrAccumulate(
                    { nameIsFree(entry, entries).bindNel() },
                    { decorateEntry(entry, scope).bindNel() }
                ) { _, _ -> }
            }.bindNel()
        }
    }

    private fun nameIsFree(entry: EntryDeclaration, entries: List<EntryDeclaration>): Diagnosed<Unit> = either {
        val first = entries.first { it.name == entry.name }
        ensure(first === entry) { EntryAlreadyDeclared(entry.parserContext, entry.name, first.parserContext).nel() }
    }

    private fun decorateEntry(entry: EntryDeclaration, globalScope: Scope): Diagnosed<Unit> {
        val entryScope = globalScope.createFunctionChild(VoidType)
        entry.context.relevantScope = entryScope

        return statementDecorator.decorateStatement(entry.body, StatementDecoratorContext(entryScope))
    }
}
