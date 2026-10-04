package me.eriknikli.rhenium.semanticContext.scope

class Scope(private val isLoop: Boolean = false) {
    private var parent: Scope? = null

    private val symbolTable = HashMap<String, Symbol>()

    fun createChild(isLoop: Boolean = false): Scope {
        val child = Scope(isLoop)
        child.parent = this
        return child
    }

    fun isInsideLoop(): Boolean = isLoop || parent?.isInsideLoop() == true

    fun getSymbol(name: String): Symbol? {
        val directSymbol = getDirectSymbol(name)
        if (directSymbol != null) {
            return directSymbol
        }

        return parent?.getSymbol(name)
    }

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
