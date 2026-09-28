package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.IfAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IIfTranspiler : IKindTranspiler<IfAction>

@Singleton
class CIfTranspiler
@Inject
constructor() : IIfTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    @Inject
    lateinit var blockActionTranspilerProvider: Lazy<IBlockActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }
    private val blockActionTranspiler by lazy { blockActionTranspilerProvider.get() }

    override fun transpile(action: IfAction, output: OutputStream) {
        output.writeText("if(")
        actionTranspiler.transpile(action.condition, output)
        output.writeText(")")
        blockActionTranspiler.transpile(action.thenBranch, output)
        action.elseBranch?.let {
            output.writeText("else ")
            actionTranspiler.transpile(it, output)
        }
    }
}
