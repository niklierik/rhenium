package me.eriknikli.rhenium.semanticContext.tree

import me.eriknikli.rhenium.semanticContext.scope.Scope

interface Context {
    var relevantScope: Scope
}