package me.eriknikli.rhenium.transpiler.actions

import me.eriknikli.rhenium.lowering.actions.BreakAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IBreakTranspiler : IKindTranspiler<BreakAction>

@Singleton
class CBreakTranspiler
@Inject
constructor() : IBreakTranspiler {
    override fun transpile(action: BreakAction, output: OutputStream) {
        output.writeText("break;")
    }
}
