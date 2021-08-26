package com.developersam.fp

import java.util.Deque
import java.util.LinkedList

sealed class FpMap<K : Comparable<K>, V> : Iterable<Pair<K, V>> {
    private object Leaf : FpMap<Nothing, Nothing>() {
        override val height: Int = 0
        override fun equals(other: Any?): Boolean = this === Leaf
        override fun hashCode(): Int = 42
    }

    private data class Node<K : Comparable<K>, V>(
            val left: FpMap<K, V>, val key: K, val value: V, val right: FpMap<K, V>,
            override val height: Int
    ) : FpMap<K, V>() {
        constructor(left: FpMap<K, V>, key: K, value: V, right: FpMap<K, V>) : this(
                left = left, key = key, value = value, right = right,
                height = if (left.height >= right.height) left.height + 1 else right.height + 1
        )
    }

    private val bstInvariantHolds: Boolean
        get() {
            val bindings = bindings.toArrayList()
            // check BST invariant
            for (i in 0 until (bindings.size - 1)) {
                val k1 = bindings[i].first
                val k2 = bindings[i + 1].first
                if (k1 > k2) {
                    return false
                }
            }
            return true
        }

    private val avlInvariantHolds: Boolean
        get() = when (this) {
            Leaf -> true
            is Node<K, V> -> (Math.abs(left.height - right.height) < 2) &&
                    left.avlInvariantHolds && right.avlInvariantHolds
        }

    internal val invariantHolds: Boolean get() = bstInvariantHolds && avlInvariantHolds

    val isEmpty: Boolean get() = this == Leaf

    protected abstract val height: Int

    val size: Int
        get() = when (this) {
            Leaf -> 0
            is Node<K, V> -> 1 + left.size + right.size
        }

    operator fun contains(key: K): Boolean = when (this) {
        Leaf -> false
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            when {
                c == 0 -> true
                c < 0 -> left.contains(key = key)
                else -> right.contains(key = key)
            }
        }
    }

    operator fun get(key: K): V? = when (this) {
        Leaf -> null
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            when {
                c == 0 -> value
                c < 0 -> left[key]
                else -> right[key]
            }
        }
    }

    fun put(key: K, value: V): FpMap<K, V> = when (this) {
        Leaf -> Node(left = empty(), key = key, value = value, right = empty(), height = 1)
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            if (c == 0) {
                if (value == this.value) {
                    this
                } else {
                    Node(left = left, key = key, value = value, right = right, height = height)
                }
            } else if (c < 0) {
                balance(
                        left = left.put(key = key, value = value), right = right,
                        key = this.key, value = this.value
                )
            } else {
                balance(
                        left = left, right = right.put(key = key, value = value),
                        key = this.key, value = this.value
                )
            }
        }
    }

    private fun removeFirstBinding(): FpMap<K, V> = when (this) {
        Leaf -> empty()
        is Node<K, V> -> when (left) {
            Leaf -> right
            else -> balance(
                    left = left.removeFirstBinding(), right = right, key = key, value = value
            )
        }
    }

    fun remove(key: K): FpMap<K, V> = when (this) {
        Leaf -> empty()
        is Node<K, V> -> {
            val c = key.compareTo(this.key)
            when {
                c == 0 -> when {
                    left == Leaf -> right
                    right == Leaf -> left
                    else -> {
                        val rightMin = right.firstBinding!!
                        balance(
                                left = left, right = right.removeFirstBinding(),
                                key = rightMin.first, value = rightMin.second
                        )
                    }
                }
                c < 0 -> balance(
                        left = left.remove(key = key), right = right,
                        key = this.key, value = this.value
                )
                else -> balance(
                        left = left, right = right.remove(key = key),
                        key = this.key, value = this.value
                )
            }
        }
    }

    fun forEach(action: (K, V) -> Unit): Unit = when (this) {
        Leaf -> Unit
        is Node<K, V> -> {
            left.forEach(action = action)
            action(key, value)
            right.forEach(action = action)
        }
    }

    fun <R> fold(initial: R, operation: (K, V, R) -> R): R = when (this) {
        Leaf -> initial
        is Node<K, V> -> right.fold(
                initial = operation(key, value, left.fold(initial, operation)),
                operation = operation
        )
    }

    fun exists(predicate: (K, V) -> Boolean): Boolean = when (this) {
        Leaf -> false
        is Node<K, V> -> {
            val stack: Deque<Node<K, V>> = LinkedList()
            stack.addLast(this)
            var exists = false
            while (stack.isNotEmpty()) {
                val node = stack.pollLast()
                if (predicate(node.key, node.value)) {
                    exists = true
                    break
                }
                (node.right as? Node<K, V>)?.run { stack.addLast(this) }
                (node.left as? Node<K, V>)?.run { stack.addLast(this) }
            }
            exists
        }
    }

    fun all(predicate: (K, V) -> Boolean): Boolean = when (this) {
        Leaf -> true
        is Node<K, V> -> {
            val stack: Deque<Node<K, V>> = LinkedList()
            stack.addLast(this)
            var all = true
            while (stack.isNotEmpty()) {
                val node = stack.pollLast()
                if (!predicate(node.key, node.value)) {
                    all = false
                    break
                }
                (node.right as? Node<K, V>)?.run { stack.addLast(this) }
                (node.left as? Node<K, V>)?.run { stack.addLast(this) }
            }
            all
        }
    }

    inline fun filter(crossinline predicate: (K, V) -> Boolean): FpMap<K, V> =
            fold(initial = empty()) { k, v, acc ->
                if (predicate(k, v)) acc.put(key = k, value = v) else acc
            }

    inline fun partition(crossinline predicate: (K, V) -> Boolean): Pair<FpMap<K, V>, FpMap<K, V>> =
            fold(initial = Pair(first = empty(), second = empty())) { k, v, acc ->
                if (predicate(k, v)) {
                    acc.first.put(key = k, value = v) to acc.second
                } else {
                    acc.first to acc.second.put(key = k, value = v)
                }
            }

    val bindings: FpList<Pair<K, V>>
        get() = fold<FpList<Pair<K, V>>>(initial = FpList.empty()) { k, v, l -> (k to v) cons l }
                .reverse

    val firstBinding: Pair<K, V>?
        get() {
            var curr = this
            if (curr == Leaf) {
                return null
            }
            while (true) {
                val (left, key, value) = curr as Node<K, V>
                if (left == Leaf) {
                    return key to value
                }
                curr = left
            }
        }

    val lastBinding: Pair<K, V>?
        get() {
            var curr = this
            if (curr == Leaf) {
                return null
            }
            while (true) {
                val (_, key, value, right) = curr as Node<K, V>
                if (right == Leaf) {
                    return key to value
                }
                curr = right
            }
        }

    fun peek(): Pair<K, V>? = when (this) {
        Leaf -> null
        is Node<K, V> -> key to value
    }

    inline fun <K2 : Comparable<K2>> mapByKey(crossinline transform: (K) -> K2): FpMap<K2, V> =
            fold(initial = empty()) { k, v, acc -> acc.put(key = transform(k), value = v) }

    inline fun <V2> mapByValue(crossinline transform: (V) -> V2): FpMap<K, V2> =
            fold(initial = empty()) { k, v, acc -> acc.put(key = k, value = transform(v)) }

    inline fun <K2 : Comparable<K2>, V2> mapByKeyValuePair(
            crossinline transform: (K, V) -> Pair<K2, V2>
    ): FpMap<K2, V2> = fold(initial = empty()) { k, v, acc ->
        val result = transform(k, v)
        acc.put(key = result.first, value = result.second)
    }

    final override fun toString(): String = map { (k, v) -> "($k, $v)" }.toString()

    override fun iterator(): Iterator<Pair<K, V>> = this.bindings.iterator()

    companion object {
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <K : Comparable<K>, V> empty(): FpMap<K, V> = Leaf as FpMap<K, V>

        fun <K : Comparable<K>, V> singleton(key: K, value: V): FpMap<K, V> =
                FpMap.Node(left = empty(), key = key, value = value, right = empty())

        fun <K : Comparable<K>, V> create(vararg pairs: Pair<K, V>): FpMap<K, V> {
            var map: FpMap<K, V> = empty()
            for ((k, v) in pairs) {
                map = map.put(key = k, value = v)
            }
            return map
        }

        fun <K : Comparable<K>, V> create(pairs: FpList<Pair<K, V>>): FpMap<K, V> =
                pairs.fold(initial = empty()) { acc, (key, value) -> acc.put(key, value) }

        private fun <K : Comparable<K>, V> balance(
                left: FpMap<K, V>, right: FpMap<K, V>, key: K, value: V
        ): FpMap<K, V> {
            val leftHeight = left.height
            val rightHeight = right.height
            return when {
                leftHeight >= rightHeight + 2 -> when (left) {
                    Leaf -> throw Error("Impossible")
                    is Node<K, V> -> {
                        val leftLeft = left.left
                        val leftRight = left.right
                        if (leftLeft.height >= leftRight.height) {
                            val r = Node(left = leftRight, key = key, value = value, right = right)
                            Node(left = leftLeft, key = left.key, value = left.value, right = r)
                        } else when (leftRight) {
                            Leaf -> throw Error("Impossible")
                            is Node<K, V> -> {
                                val leftRightLeft = leftRight.left
                                val leftRightRight = leftRight.right
                                Node(
                                        left = Node(
                                                left = leftLeft, key = left.key,
                                                value = left.value, right = leftRightLeft
                                        ),
                                        key = leftRight.key, value = leftRight.value,
                                        right = Node(
                                                left = leftRightRight, key = key,
                                                value = value, right = right
                                        )
                                )
                            }
                        }
                    }
                }
                rightHeight >= leftHeight + 2 -> when (right) {
                    Leaf -> throw Error("Impossible")
                    is Node<K, V> -> {
                        val rightLeft = right.left
                        val rightRight = right.right
                        if (rightRight.height >= rightLeft.height) {
                            val l = Node(left = left, key = key, value = value, right = rightLeft)
                            Node(left = l, key = right.key, value = right.value, right = rightRight)
                        } else when (rightLeft) {
                            Leaf -> throw Error("Impossible")
                            is Node<K, V> -> {
                                val rightLeftLeft = rightLeft.left
                                val rightLeftRight = rightLeft.right
                                Node(
                                        left = Node(
                                                left = left, key = key,
                                                value = value, right = rightLeftLeft
                                        ),
                                        key = rightLeft.key, value = rightLeft.value,
                                        right = Node(
                                                left = rightLeftRight, key = right.key,
                                                value = right.value, right = rightRight
                                        )
                                )
                            }
                        }
                    }
                }
                else -> Node(left = left, right = right, key = key, value = value)
            }
        }
    }

}
