package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.ForAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IForTranspiler : IKindTranspiler<ForAction>

@Singleton
class CForTranspiler
@Inject
constructor() : IForTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    @Inject
    lateinit var blockActionTranspilerProvider: Lazy<IBlockActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }
    private val blockActionTranspiler by lazy { blockActionTranspilerProvider.get() }

    override fun transpile(action: ForAction, output: OutputStream) {
        output.writeText("for(")
        actionTranspiler.transpile(action.initClause, output)
        actionTranspiler.transpile(action.condition, output)
        output.writeText(";")
        actionTranspiler.transpile(action.iterationExpression, output)
        output.writeText(")")
        blockActionTranspiler.transpile(action.body, output)
    }
}
