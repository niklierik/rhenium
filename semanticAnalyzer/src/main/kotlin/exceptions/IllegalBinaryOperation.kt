package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.ast.tree.expressions.operators.Operator
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

class IllegalBinaryOperation(
    parserContext: ParserRuleContext,
    left: ExpressionType,
    right: ExpressionType,
    operator: Operator
) : SemanticException() {
}