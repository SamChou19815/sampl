package com.developersam.fp

class FpSet<V : Comparable<V>>(val m: FpMap<V, Unit>) : Iterable<V> {
    val isEmpty: Boolean get() = m.isEmpty
    val size: Int get() = m.size
    operator fun contains(value: V): Boolean = m.contains(key = value)
    fun add(value: V): FpSet<V> = FpSet(m = m.put(key = value, value = Unit))
    fun remove(value: V): FpSet<V> = FpSet(m = m.remove(key = value))
    fun union(another: FpSet<V>): FpSet<V> = fold(initial = another) { acc, v -> acc.add(v) }
    fun intersection(another: FpSet<V>): FpSet<V> = fold(initial = another) { acc, v -> if (v in this) acc.add(v) else acc }
    operator fun minus(another: FpSet<V>): FpSet<V> = fold(initial = another) { acc, v -> acc.remove(v) }
    fun isSubsetOf(another: FpSet<V>): Boolean = forAll { it in another }
    inline fun <T : Comparable<T>> map(crossinline transform: (V) -> T): FpSet<T> = FpSet(m = m.mapByKey(transform = transform))
    inline fun forEach(crossinline action: (V) -> Unit): Unit = m.forEach { v, _ -> action(v) }
    inline fun <R> fold(initial: R, crossinline operation: (R, V) -> R): R = m.fold(initial = initial) { v, _, a -> operation(a, v) }
    inline fun exists(crossinline predicate: (V) -> Boolean): Boolean = m.exists { v, _ -> predicate(v) }
    inline fun forAll(crossinline predicate: (V) -> Boolean): Boolean = m.all { v, _ -> predicate(v) }
    inline fun filter(crossinline predicate: (V) -> Boolean): FpSet<V> = FpSet(m = m.filter { v, _ -> predicate(v) })

    inline fun partition(crossinline predicate: (V) -> Boolean): Pair<FpSet<V>, FpSet<V>> {
        val (first, second) = m.partition { v, _ -> predicate(v) }
        return FpSet(m = first) to FpSet(m = second)
    }

    val elements: FpList<V> get() = m.bindings.map { it.first }
    val minElement: V? get() = m.firstBinding?.first
    val maxElement: V? get() = m.lastBinding?.first
    fun peek(): V? = m.peek()?.first
    override fun toString(): String = toList().toString()
    override fun iterator(): Iterator<V> = m.bindings.asSequence().map { it.first }.iterator()

    companion object {
        @Suppress(names = ["UNCHECKED_CAST"])
        fun <V : Comparable<V>> empty(): FpSet<V> = FpSet(m = FpMap.empty())

        fun <V : Comparable<V>> singleton(value: V): FpSet<V> =
                FpSet(m = FpMap.singleton(key = value, value = Unit))

        fun <V : Comparable<V>> create(vararg values: V): FpSet<V> {
            var set = empty<V>()
            for (v in values) {
                set = set.add(value = v)
            }
            return set
        }

        fun <V : Comparable<V>> create(list: FpList<V>): FpSet<V> =
                list.fold(initial = empty()) { acc, v -> acc.add(value = v) }
    }
}
