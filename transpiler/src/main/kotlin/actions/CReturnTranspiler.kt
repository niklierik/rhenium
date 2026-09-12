package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.ReturnAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IReturnTranspiler : IKindTranspiler<ReturnAction>

@Singleton
class CReturnTranspiler
@Inject
constructor() : IReturnTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: ReturnAction, output: OutputStream) {
        output.writeText("return")

        action.value?.let {
            output.writeText(" ")
            actionTranspiler.transpile(it, output)
        }

        output.writeText(";")
    }
}
