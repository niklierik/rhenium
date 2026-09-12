package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.TernaryAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ITernaryTranspiler : IActionTranspiler<TernaryAction>

@Singleton
class CTernaryTranspiler
@Inject
constructor() : ITernaryTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IAnyActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: TernaryAction, output: OutputStream) {
        output.writeText("(")
        actionTranspiler.transpile(action.condition, output)
        output.writeText("?")
        actionTranspiler.transpile(action.ifTrue, output)
        output.writeText(":")
        actionTranspiler.transpile(action.ifFalse, output)
        output.writeText(")")
    }
}
