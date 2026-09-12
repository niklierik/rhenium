package me.eriknikli.rhenium.transpiler

import me.eriknikli.rhenium.lowering.actions.Action
import java.io.OutputStream

interface IActionTranspiler<in A : Action> {
    fun transpile(action: A, output: OutputStream)
}
