package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.semanticContext.scope.Scope
import javax.inject.Inject
import javax.inject.Singleton

data class StatementDecoratorContext(
    val scope: Scope
)

interface IStatementNodeDecorator {
    fun decorateStatement(statement: Statement, statementDecoratorContext: StatementDecoratorContext)
}

@Singleton
class StatementNodeDecorator
@Inject
constructor(

) : IStatementNodeDecorator {
    @Inject
    lateinit var varDeclStatementDecoratorProvider: Lazy<IVarDeclarationStatementDecorator>

    private val varDeclStatementDecorator by lazy { varDeclStatementDecoratorProvider.get() }


    override fun decorateStatement(
        statement: Statement,
        statementDecoratorContext: StatementDecoratorContext
    ) {
        val scope = statementDecoratorContext.scope
        statement.context.relevantScope = scope

        when (statement) {
            is VarDeclarationStatement -> varDeclStatementDecorator.decorate(statement)

            else -> throw IllegalStateException("Unhandled node ${statement.javaClass}")
        }
    }
}

