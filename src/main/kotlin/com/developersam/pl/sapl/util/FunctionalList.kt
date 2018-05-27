package com.developersam.pl.sapl.util

/**
 * [FunctionalList] is a single-ly linked list in a functional way.
 * Methods in this class are generally tail recursive. Non-tail-recursive ones are marked.
 *
 * @param T type of the elements in the list.
 */
sealed class FunctionalList<out T> : Iterable<T> {

    /**
     * [length] reports the length of the list.
     */
    val length: Int
        get() = this.reduceFromLeft(acc = 0) { acc, _ -> acc + 1 }

    /**
     * [reduceFromLeft] reduces the list to a value from left, with the accumulator initialized to
     * be [acc] and a reducer [f].
     */
    tailrec fun <R> reduceFromLeft(acc: R, f: (R, T) -> R): R = when (this) {
        is FunctionalListNil -> acc
        is FunctionalListNode<T> -> reduceFromLeft(acc = f(acc, data), f = f)
    }

    /**
     * [reduceFromLeft] reduces the list to a value from right, with the initial value [init] and
     * a reducer [f].
     * It is NOT tail recursive.
     */
    fun <R> reduceFromRight(init: R, f: (T, R) -> R): R = when (this) {
        is FunctionalListNil -> init
        is FunctionalListNode<T> -> f(data, next.reduceFromRight(init = init, f = f))
    }

    override fun iterator(): Iterator<T> {
        val me = this
        return object : Iterator<T> {

            private var curr: FunctionalList<T> = me

            override fun hasNext(): Boolean = me !== FunctionalListNil

            override fun next(): T {
                val c = curr
                return when (c) {
                    is FunctionalListNil -> throw Error("Improper Usage!")
                    is FunctionalListNode<T> -> {
                        val v = c.data
                        curr = c.next
                        v
                    }
                }
            }
        }
    }

    companion object {
        /**
         * [singletonList] creates a singleton list that contains only [data].
         */
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <T> singletonList(data: T): FunctionalList<T> =
                FunctionalListNode(data = data, next = FunctionalListNil as FunctionalList<T>)
    }

}

/**
 * [FunctionalListNil] is the end of the list. It is `[]` in OCaml.
 */
object FunctionalListNil : FunctionalList<Nothing>() {
    override fun toString(): String = "Nil"
    override fun equals(other: Any?): Boolean = this === FunctionalListNil
    override fun hashCode(): Int = 42
}

/**
 * [FunctionalListNode] is a node that contains some [data] and an immutable pointer to the next
 * element [next].
 */
data class FunctionalListNode<T>(val data: T, val next: FunctionalList<T>) : FunctionalList<T>()

/**
 * [FunctionalList.cons] creates a new list with [data] at front and everything else in this
 * list behind [data].
 */
fun <T> FunctionalList<T>.cons(data: T): FunctionalList<T> =
        FunctionalListNode(data = data, next = this)
