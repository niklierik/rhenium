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

fun <T, R> Iterable<T>.mapAllAndThrow(run: (it: T) -> R): Iterable<R> {
    val exceptions = ArrayList<Exception>()
    val result = mapNotNull {
        try {
            run(it)
        } catch (exception: Exception) {
            exceptions.add(exception)
            null
        }
    }
    if (exceptions.size > 0) {
        throw AggregateException(*exceptions.toTypedArray())
    }

    return result
}


fun <T> Iterable<T>.forEachAllAndThrow(run: (it: T) -> Unit): Iterable<Unit> {
    return this.mapAllAndThrow(run)
}

fun <TLeft, TRight> and(left: () -> TLeft, right: () -> TRight): Pair<TLeft, TRight> {
    val exceptions = ArrayList<Exception>()
    var leftResult: TLeft? = null
    try {
        leftResult = left()
    } catch (leftException: Exception) {
        exceptions.add(leftException)
    }
    var rightResult: TRight? = null
    try {
        rightResult = right()
    } catch (rightException: Exception) {
        exceptions.add(rightException)
    }
    if (exceptions.size > 0) {
        throw AggregateException(*exceptions.toTypedArray())
    }
    return Pair<TLeft, TRight>(leftResult as TLeft, rightResult as TRight)
}

fun <T> (() -> T).throwInstead(onError: (it: Exception) -> Exception): T {
    return try {
        this()
    } catch (exception: Exception) {
        throw onError(exception)
    }
}


inline fun <T> (() -> T).throwInsteadIf(
    condition: (it: Exception) -> Boolean,
    onError: (it: Exception) -> Exception
): T {
    return try {
        this()
    } catch (exception: Exception) {
        if (condition(exception)) {
            throw onError(exception)
        }
        throw exception
    }
}