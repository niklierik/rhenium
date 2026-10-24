package me.eriknikli.rhenium.semanticContext.tree.declarations

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.functions.FunctionSymbol
import me.eriknikli.rhenium.semanticContext.tree.Context

class FunctionDeclarationContext : Context {
    override lateinit var relevantScope: Scope
    lateinit var symbol: FunctionSymbol
}
