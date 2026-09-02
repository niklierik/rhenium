package me.eriknikli.rhenium.semanticAnalyzer.statements

import dagger.Lazy
import me.eriknikli.rhenium.ast.tree.statements.ExpressionStatement
import me.eriknikli.rhenium.ast.tree.statements.PrintStatement
import me.eriknikli.rhenium.ast.tree.statements.Statement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarAssignmentStatement
import me.eriknikli.rhenium.ast.tree.statements.vars.VarDeclarationStatement
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticContext.scope.Scope
import javax.inject.Inject
import javax.inject.Singleton

data class StatementDecoratorContext(
    val scope: Scope
)

interface IStatementNodeDecorator {
    fun decorateStatement(
        statement: Statement,
        statementDecoratorContext: StatementDecoratorContext
    ): Diagnosed<Unit>
}

@Singleton
class StatementNodeDecorator
@Inject
constructor(

) : IStatementNodeDecorator {
    @Inject
    lateinit var varDeclStatementDecoratorProvider: Lazy<IVarDeclarationStatementDecorator>

    private val varDeclStatementDecorator by lazy { varDeclStatementDecoratorProvider.get() }

    @Inject
    lateinit var varAssignmentStatementDecoratorProvider: Lazy<IVarAssignmentStatementDecorator>
    private val varAssignmentStatementDecorator by lazy { varAssignmentStatementDecoratorProvider.get() }

    @Inject
    lateinit var expressionStatementDecoratorProvider: Lazy<IExpressionStatementDecorator>
    private val expressionStatementDecorator by lazy { expressionStatementDecoratorProvider.get() }

    @Inject
    lateinit var printStatementDecoratorProvider: Lazy<IPrintStatementDecorator>
    private val printStatementDecorator by lazy { printStatementDecoratorProvider.get() }

    override fun decorateStatement(
        statement: Statement,
        statementDecoratorContext: StatementDecoratorContext
    ): Diagnosed<Unit> {
        val scope = statementDecoratorContext.scope
        statement.context.relevantScope = scope

        return when (statement) {
            is VarDeclarationStatement -> varDeclStatementDecorator.decorate(statement)
            is VarAssignmentStatement -> varAssignmentStatementDecorator.decorate(
                statement,
                StatementDecoratorContext(scope)
            )

            is ExpressionStatement -> expressionStatementDecorator.decorate(
                statement,
                StatementDecoratorContext(scope)
            )

            is PrintStatement -> printStatementDecorator.decorate(
                statement,
                StatementDecoratorContext(scope)
            )

            else -> throw IllegalStateException(
                "Unhandled statement node ${statement.javaClass.simpleName} cannot be decorated."
            )
        }
    }
}
