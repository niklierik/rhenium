package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.scope.LeftValueSymbol

interface LeftValueContext : ExpressionContext {
    var symbol: LeftValueSymbol
}