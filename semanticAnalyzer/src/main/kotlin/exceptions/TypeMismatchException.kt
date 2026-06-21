package me.eriknikli.rhenium.semanticAnalyzer.exceptions

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import org.antlr.v4.runtime.ParserRuleContext

class TypeMismatchException(
    val parserRuleContext: ParserRuleContext,
    val actual: ExpressionType,
    vararg val expected: ExpressionType
) : SemanticException() {
}