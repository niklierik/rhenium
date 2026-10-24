package me.eriknikli.rhenium.semanticContext.tree.declarations

import me.eriknikli.rhenium.semanticContext.scope.Scope
import me.eriknikli.rhenium.semanticContext.tree.Context

class EntryDeclarationContext : Context {
    override lateinit var relevantScope: Scope
}
