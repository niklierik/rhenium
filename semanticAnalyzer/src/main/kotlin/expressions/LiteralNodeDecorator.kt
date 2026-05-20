package me.eriknikli.rhenium.semanticAnalyzer.expressions

import me.eriknikli.rhenium.ast.tree.expressions.literals.*
import me.eriknikli.rhenium.semanticContext.scope.types.BooleanType
import me.eriknikli.rhenium.semanticContext.scope.types.FloatType
import me.eriknikli.rhenium.semanticContext.scope.types.SignedIntType
import me.eriknikli.rhenium.semanticContext.scope.types.UnsignedIntType
import javax.inject.Inject
import javax.inject.Singleton

interface ILiteralNodeDecorator {
    fun decorateLiteral(literal: Literal<*>)
}

@Singleton
class LiteralNodeDecorator
@Inject
constructor() : ILiteralNodeDecorator {
    override fun decorateLiteral(literal: Literal<*>) {
        when (literal) {
            is BooleanLiteral ->
                literal.context.type = BooleanType()

            is F64Literal ->
                literal.context.type = FloatType.F64

            is F32Literal ->
                literal.context.type = FloatType.F32

            is U64Literal ->
                literal.context.type = UnsignedIntType.U64

            is U32Literal ->
                literal.context.type = UnsignedIntType.U32

            is U16Literal ->
                literal.context.type = UnsignedIntType.U16

            is U8Literal ->
                literal.context.type = UnsignedIntType.U8

            is I64Literal ->
                literal.context.type = SignedIntType.I64

            is I32Literal ->
                literal.context.type = SignedIntType.I32

            is I16Literal ->
                literal.context.type = SignedIntType.I16

            is I8Literal ->
                literal.context.type = SignedIntType.I8
        }
    }
}