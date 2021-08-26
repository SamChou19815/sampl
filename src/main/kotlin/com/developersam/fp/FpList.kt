package com.developersam.fp

sealed class FpList<out T> : Iterable<T> {
    object Nil : FpList<Nothing>() {
        override fun equals(other: Any?): Boolean = this === Nil
        override fun hashCode(): Int = 42
    }

    data class Node<T>(val data: T, val next: FpList<T>) : FpList<T>()

    override fun iterator(): Iterator<T> = object : Iterator<T> {

        private var curr: FpList<T> = this@FpList

        override fun hasNext(): Boolean = curr != Nil

        override fun next(): T {
            val immutableCurr = curr as? Node<T> ?: error(message = "Wrong use of iterator!")
            val data = immutableCurr.data
            curr = immutableCurr.next
            return data
        }
    }

    companion object {
        fun <T> empty(): FpList<T> = Nil
        fun <T> singleton(data: T): FpList<T> = Node(data = data, next = empty())

        fun <T> create(vararg values: T): FpList<T> {
            var list = empty<T>()
            for (i in (values.size - 1) downTo 0) {
                list = values[i] cons list
            }
            return list
        }
    }

    val isEmpty: Boolean get() = this == Nil

    val size: Int get() = fold(initial = 0) { acc, _ -> acc + 1 }

    val head: T
        get() = when (this) {
            Nil -> NotFoundError.raise()
            is Node<T> -> data
        }

    val tail: FpList<T>
        get() = when (this) {
            Nil -> NotFoundError.raise()
            is Node<T> -> next
        }

    val reverse: FpList<T>
        get() = fold(initial = Nil as FpList<T>) { acc, data -> data cons acc }

    fun <R> map(transform: (T) -> R): FpList<R> = when (this) {
        FpList.Nil -> Nil
        is Node<T> -> Node(data = transform(data), next = next.map(transform))
    }

    inline fun <reified R> fold(initial: R, crossinline operation: (R, T) -> R): R {
        var curr = this
        var acc = initial
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            acc = operation(acc, data)
            curr = next
        }
        return acc
    }

    fun <R> foldRight(initial: R, operation: (R, T) -> R): R = when (this) {
        Nil -> initial
        is Node<T> -> operation(
                next.foldRight(initial = initial, operation = operation), data
        )
    }

    inline fun forEach(crossinline action: (T) -> Unit) {
        var curr = this
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            action(data)
            curr = next
        }
    }

    inline fun all(crossinline predicate: (T) -> Boolean): Boolean {
        var curr = this
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            if (!predicate(data)) {
                return false
            }
            curr = next
        }
        return true
    }

    inline fun exists(crossinline predicate: (T) -> Boolean): Boolean {
        var curr = this
        while (curr != Nil) {
            val (data, next) = curr as Node<T>
            if (predicate(data)) {
                return true
            }
            curr = next
        }
        return false
    }

    final override fun toString(): String = toList().toString()

}

infix fun <T> T.cons(list: FpList<T>): FpList<T> = FpList.Node(data = this, next = list)

fun <T> FpList<T>.append(another: FpList<T>): FpList<T> =
        foldRight(initial = another) { list, data -> data cons list }

val <T> FpList<FpList<T>>.flatten: FpList<T>
    get() = when (this) {
        FpList.Nil -> FpList.Nil
        is FpList.Node<FpList<T>> -> data.append(another = next.flatten)
    }

operator fun <T> FpList<T>.contains(element: T): Boolean {
    var curr = this
    while (curr != FpList.Nil) {
        val (data, next) = curr as FpList.Node<T>
        if (element == data) {
            return true
        }
        curr = next
    }
    return false
}
@Suppress(names = ["UNCHECKED_CAST"])
fun <T> FpList<T>.toArrayList(): List<T> {
    val tempList = arrayListOf<T>()
    var list = this
    while (list != FpList.Nil) {
        tempList.add(list.head)
        list = list.tail
    }
    return tempList
}
