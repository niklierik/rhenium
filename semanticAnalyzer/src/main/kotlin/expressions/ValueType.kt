package me.eriknikli.rhenium.semanticAnalyzer.expressions

import arrow.core.flatMap
import arrow.core.leftNel
import arrow.core.right
import me.eriknikli.rhenium.common.diagnostics.Diagnosed
import me.eriknikli.rhenium.semanticAnalyzer.diagnostics.VoidHasNoValue
import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType
import me.eriknikli.rhenium.semanticContext.scope.types.VoidType
import org.antlr.v4.runtime.ParserRuleContext

fun Diagnosed<ExpressionType>.asValueType(source: ParserRuleContext): Diagnosed<ExpressionType> = flatMap {
    if (it is VoidType) VoidHasNoValue(source).leftNel() else it.right()
}
