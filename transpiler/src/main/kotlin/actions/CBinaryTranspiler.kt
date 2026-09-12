package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.BinaryAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IBinaryTranspiler : IActionTranspiler<BinaryAction>

@Singleton
class CBinaryTranspiler
@Inject
constructor() : IBinaryTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IAnyActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: BinaryAction, output: OutputStream) {
        output.writeText("(")
        actionTranspiler.transpile(action.left, output)
        output.writeText(action.cOperator)
        actionTranspiler.transpile(action.right, output)
        output.writeText(")")
    }
}
