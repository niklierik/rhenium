package me.eriknikli.rhenium.transpiler.actions

import dagger.Lazy
import me.eriknikli.rhenium.lowering.actions.ProgramAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeLineBreak
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IProgramTranspiler : IKindTranspiler<ProgramAction>

@Singleton
class CProgramTranspiler
@Inject
constructor() : IProgramTranspiler {
    @Inject
    lateinit var actionTranspilerProvider: Lazy<IActionTranspiler>

    private val actionTranspiler by lazy { actionTranspilerProvider.get() }

    override fun transpile(action: ProgramAction, output: OutputStream) {
        action.declarations.forEach {
            actionTranspiler.transpile(it, output)
            output.writeLineBreak()
        }
    }
}
