package me.eriknikli.rhenium.semanticAnalyzer.declarations

import arrow.core.flatMap
import arrow.core.leftNel
import arrow.core.right
import me.eriknikli.rhenium.ast.tree.expressions.Identifier
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.UnknownType
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.VoidHasNoValue
import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import javax.inject.Inject
import javax.inject.Singleton

interface ITypeNameDecorator {
    fun typeOf(typeName: Identifier, scope: Scope): Diagnosed<ExpressionType>

    fun valueTypeOf(typeName: Identifier, scope: Scope): Diagnosed<ExpressionType>
}

@Singleton
class TypeNameDecorator
@Inject
constructor() : ITypeNameDecorator {
    override fun typeOf(typeName: Identifier, scope: Scope): Diagnosed<ExpressionType> {
        val symbol = scope.getSymbol(typeName.id)

        if (symbol !is ExpressionType) {
            return UnknownType(typeName.parserContext, typeName.id).leftNel()
        }

        return symbol.right()
    }

    override fun valueTypeOf(typeName: Identifier, scope: Scope): Diagnosed<ExpressionType> =
        typeOf(typeName, scope).flatMap {
            if (it is VoidType) VoidHasNoValue(typeName.parserContext).leftNel() else it.right()
        }
}
