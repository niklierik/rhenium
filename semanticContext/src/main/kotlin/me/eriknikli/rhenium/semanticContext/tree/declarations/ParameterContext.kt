package me.eriknikli.rhenium.semanticContext.tree.declarations

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.scope.vars.Variable
import me.eriknikli.rhenium.semanticContext.tree.Context

class ParameterContext : Context {
    override lateinit var relevantScope: Scope
    lateinit var variable: Variable
}
