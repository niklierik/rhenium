package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.ExpressionStatementAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IExpressionStatementTranspiler : IActionTranspiler<ExpressionStatementAction>

@Singleton
class CExpressionStatementTranspiler
@Inject
constructor() : IExpressionStatementTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IAnyActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: ExpressionStatementAction, output: OutputStream) {
        actionTranspiler.transpile(action.value, output)
        output.writeText(";")
    }
}
