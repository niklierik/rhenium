package me.eriknikli.rhenium.transpiler.utils

import me.eriknikli.rhenium.lowering.actions.FunctionSignature
import java.io.OutputStream

fun OutputStream.writeSignature(signature: FunctionSignature) {
    val parameters = signature.parameters.joinToString(",") { "${it.type.cName} ${it.cName}" }.ifEmpty { "void" }
    writeText("${signature.returnType.cName} ${signature.cName}($parameters)")
}
