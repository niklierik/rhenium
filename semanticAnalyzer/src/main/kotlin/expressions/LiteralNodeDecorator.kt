package me.eriknikli.rhenium.semanticAnalyzer.expressions

import me.eriknikli.rhenium.ast.tree.expressions.literals.*
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.FloatType
import me.eriknikli.rhenium.semanticContext.scope.types.SignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import javax.inject.Inject
import javax.inject.Singleton

interface ILiteralNodeDecorator {
    fun decorateLiteral(literal: Literal<*>): ExpressionType
}

@Singleton
class LiteralNodeDecorator
@Inject
constructor() : ILiteralNodeDecorator {
    override fun decorateLiteral(literal: Literal<*>): ExpressionType {
        val type = when (literal) {
            is BooleanLiteral -> BooleanType
            is F64Literal -> FloatType.F64
            is F32Literal -> FloatType.F32
            is U64Literal -> UnsignedIntType.U64
            is U32Literal -> UnsignedIntType.U32
            is U16Literal -> UnsignedIntType.U16
            is U8Literal -> UnsignedIntType.U8
            is I64Literal -> SignedIntType.I64
            is I32Literal -> SignedIntType.I32
            is I16Literal -> SignedIntType.I16
            is I8Literal -> SignedIntType.I8
            else -> throw IllegalStateException(
                "Unhandled literal kind ${literal.javaClass.simpleName} has no type."
            )
        }

        literal.context.type = type

        return type
    }
}
