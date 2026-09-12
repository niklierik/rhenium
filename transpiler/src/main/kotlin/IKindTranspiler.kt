package me.eriknikli.rhenium.transpiler

import me.eriknikli.rhenium.lowering.actions.Action
import java.io.OutputStream

interface IKindTranspiler<in A : Action> {
    fun transpile(action: A, output: OutputStream)
}
