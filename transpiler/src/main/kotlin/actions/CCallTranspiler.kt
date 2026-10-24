package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.CallAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ICallTranspiler : IKindTranspiler<CallAction>

@Singleton
class CCallTranspiler
@Inject
constructor() : ICallTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: CallAction, output: OutputStream) {
        output.writeText("${action.cName}(")
        action.arguments.forEachIndexed { index, argument ->
            if (index > 0) {
                output.writeText(",")
            }
            actionTranspiler.transpile(argument, output)
        }
        output.writeText(")")
    }
}
