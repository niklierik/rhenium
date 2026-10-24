package me.eriknikli.rhenium.transpiler.actions

import me.eriknikli.rhenium.lowering.actions.PrototypeAction
import me.eriknikli.rhenium.transpiler.IKindTranspiler
import me.eriknikli.rhenium.transpiler.utils.writeSignature
import me.eriknikli.rhenium.transpiler.utils.writeText
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

interface IPrototypeTranspiler : IKindTranspiler<PrototypeAction>

@Singleton
class CPrototypeTranspiler
@Inject
constructor() : IPrototypeTranspiler {
    override fun transpile(action: PrototypeAction, output: OutputStream) {
        output.writeSignature(action.signature)
        output.writeText(";")
    }
}
