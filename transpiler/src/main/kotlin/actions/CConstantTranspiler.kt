package me.eriknikli.rhenium.transpiler.actions

import me.eriknikli.rhenium.lowering.actions.ConstantAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IConstantTranspiler : IKindTranspiler<ConstantAction>

@Singleton
class CConstantTranspiler
@Inject
constructor() : IConstantTranspiler {
    override fun transpile(action: ConstantAction, output: OutputStream) {
        output.writeText(action.cLiteral)
    }
}
