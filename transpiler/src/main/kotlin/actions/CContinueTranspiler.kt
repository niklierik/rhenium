package me.eriknikli.rhenium.transpiler.actions

import me.eriknikli.rhenium.lowering.actions.ContinueAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IContinueTranspiler : IKindTranspiler<ContinueAction>

@Singleton
class CContinueTranspiler
@Inject
constructor() : IContinueTranspiler {
    override fun transpile(action: ContinueAction, output: OutputStream) {
        output.writeText("continue;")
    }
}
