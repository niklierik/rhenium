package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.AssignmentAction
import me.eriknikli.rhenium.transpiler.IActionTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IAssignmentTranspiler : IActionTranspiler<AssignmentAction>

@Singleton
class CAssignmentTranspiler
@Inject
constructor() : IAssignmentTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IAnyActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: AssignmentAction, output: OutputStream) {
        actionTranspiler.transpile(action.target, output)
        output.writeText("=")
        actionTranspiler.transpile(action.value, output)
        output.writeText(";")
    }
}
