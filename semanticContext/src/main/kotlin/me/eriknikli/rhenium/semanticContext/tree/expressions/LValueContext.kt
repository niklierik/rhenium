package me.eriknikli.rhenium.semanticContext.tree.expressions

import me.eriknikli.rhenium.semanticContext.scope.Symbol

interface LValueContext : ExpressionContext {
    var mutable: Boolean
    var symbol: Symbol
}