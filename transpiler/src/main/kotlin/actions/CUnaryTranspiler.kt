package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.UnaryAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IUnaryTranspiler : IActionTranspiler<UnaryAction>

@Singleton
class CUnaryTranspiler
@Inject
constructor() : IUnaryTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IAnyActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: UnaryAction, output: OutputStream) {
        output.writeText("(${action.cOperator}")
        actionTranspiler.transpile(action.operand, output)
        output.writeText(")")
    }
}
