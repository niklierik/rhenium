package me.eriknikli.rhenium.common

class AggregateException : Exception {
    val children: List<Throwable>

    constructor(vararg throwables: Throwable) : super(getChildren(throwables).firstOrNull()) {
        this.children = getChildren(throwables)
    }

    companion object {
        private fun getChildren(throwables: Array<out Throwable>): List<Throwable> {
            val children = ArrayList<Throwable>()
            for (throwable in throwables) {
                if (throwable is AggregateException) {
                    children.addAll(getChildren(throwable.children.toTypedArray()))
                } else {
                    children.add(throwable)
                }
            }
            return children
        }
    }
}