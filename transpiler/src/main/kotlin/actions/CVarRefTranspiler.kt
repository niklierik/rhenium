package me.eriknikli.rhenium.transpiler.actions

import me.eriknikli.rhenium.lowering.actions.VarRefAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IVarRefTranspiler : IActionTranspiler<VarRefAction>

@Singleton
class CVarRefTranspiler
@Inject
constructor() : IVarRefTranspiler {
    override fun transpile(action: VarRefAction, output: OutputStream) {
        output.writeText(action.cName)
    }
}
