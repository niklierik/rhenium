package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.PrintAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IPrintTranspiler : IKindTranspiler<PrintAction>

@Singleton
class CPrintTranspiler
@Inject
constructor() : IPrintTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: PrintAction, output: OutputStream) {
        output.writeText("printf(${action.cFormat}")

        action.value?.let {
            output.writeText(",")
            actionTranspiler.transpile(it, output)
        }

        output.writeText(");")
    }
}
