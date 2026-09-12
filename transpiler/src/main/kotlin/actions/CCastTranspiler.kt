package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.CastAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ICastTranspiler : IKindTranspiler<CastAction>

@Singleton
class CCastTranspiler
@Inject
constructor() : ICastTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: CastAction, output: OutputStream) {
        output.writeText("((${action.type.cName})")
        actionTranspiler.transpile(action.operand, output)
        output.writeText(")")
    }
}
