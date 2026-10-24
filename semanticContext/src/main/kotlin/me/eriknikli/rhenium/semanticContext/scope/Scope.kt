package me.eriknikli.rhenium.semanticContext.scope

import me.eriknikli.rhenium.semanticContext.scope.types.ExpressionType

class Scope(
    private val isLoop: Boolean = false,
    private val returnType: ExpressionType? = null
) {
    private var parent: Scope? = null

    private val symbolTable = HashMap<String, Symbol>()

    fun createChild(isLoop: Boolean = false): Scope {
        val child = Scope(isLoop)
        child.parent = this
        return child
    }

    fun createFunctionChild(returnType: ExpressionType): Scope {
        val child = Scope(returnType = returnType)
        child.parent = this
        return child
    }

    fun enclosingReturnType(): ExpressionType? = returnType ?: parent?.enclosingReturnType()

    fun isInsideLoop(): Boolean = isLoop || parent?.isInsideLoop() == true

    fun getSymbol(name: String): Symbol? {
        val directSymbol = getDirectSymbol(name)
        if (directSymbol != null) {
            return directSymbol
        }

        return parent?.getSymbol(name)
    }

    fun getType(name: String): ExpressionType? =
        getDirectSymbol(name) as? ExpressionType ?: parent?.getType(name)

    fun getDirectSymbol(name: String): Symbol? {
        val symbol = symbolTable.getOrDefault(name, null)
        if (symbol != null) {
            return symbol
        }

        return null
    }

    fun insertSymbol(name: String, symbol: Symbol) {
        symbolTable[name] = symbol
    }
}
