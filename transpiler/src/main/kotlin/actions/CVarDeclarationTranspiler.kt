package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.VarDeclarationAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IVarDeclarationTranspiler : IActionTranspiler<VarDeclarationAction>

@Singleton
class CVarDeclarationTranspiler
@Inject
constructor() : IVarDeclarationTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IAnyActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: VarDeclarationAction, output: OutputStream) {
        output.writeText("${action.type.cName} ${action.cName}=")
        actionTranspiler.transpile(action.value, output)
        output.writeText(";")
    }
}
