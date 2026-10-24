package me.eriknikli.rhenium.semanticContext.scope

import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.FloatType
import me.eriknikli.rhenium.semanticContext.scope.types.SignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType

private fun Scope.insertPrimitives() {
    insertSymbol("I64", SignedIntType.I64)
    insertSymbol("I32", SignedIntType.I32)
    insertSymbol("I16", SignedIntType.I16)
    insertSymbol("I8", SignedIntType.I8)

    insertSymbol("U64", UnsignedIntType.U64)
    insertSymbol("U32", UnsignedIntType.U32)
    insertSymbol("U16", UnsignedIntType.U16)
    insertSymbol("U8", UnsignedIntType.U8)

    insertSymbol("F64", FloatType.F64)
    insertSymbol("F32", FloatType.F32)

    insertSymbol("Boolean", BooleanType)

    insertSymbol("Void", VoidType)
}

fun globalScope(): Scope {
    val scope = Scope()

    scope.apply {
        insertPrimitives()
    }

    return scope
}