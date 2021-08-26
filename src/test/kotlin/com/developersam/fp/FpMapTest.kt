package com.developersam.fp

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FpMapTest {
    private val emptyMap: FpMap<String, Int> = FpMap.empty()
    private val singletonMap: FpMap<String, Int> = FpMap.singleton(key = "hi", value = 0)
    private val simplePair: Pair<String, Int> = "hi" to 0

    private val always: (String, Int) -> Boolean = { _, _ -> true }
    private val never: (String, Int) -> Boolean = { _, _ -> false }
    private val eq: (String, Int) -> Boolean = { k, v -> k == "hi" && v == 0 }
    private val nEq: (String, Int) -> Boolean = { k, v -> k != "hi" || v != 0 }

    @Test
    fun containsTest() {
        // empty list
        assertEquals(false, "hi" in emptyMap)
        assertEquals(false, "hello" in emptyMap)
        // singleton list
        assertEquals(true, "hi" in singletonMap)
        assertEquals(false, "hello" in singletonMap)
    }

    @Test
    fun putRemoveTest() {
        val algorithmsPair = listOf("A" to 0, "L" to 1, "G" to 2, "O" to 3,
                "R" to 4, "I" to 5, "T" to 6, "H" to 7, "M" to 8, "S" to 9)
        val l = algorithmsPair.size
        val c: Comparator<Pair<String, Int>> = Comparator { p1, p2 -> p1.first.compareTo(p2.first) }
        var map = emptyMap
        for (i in 0 until l) {
            val pair = algorithmsPair[i]
            val newMap = map.put(pair.first, pair.second)
            val l1 = newMap.bindings.toArrayList().sortedWith(c)
            val l2 = algorithmsPair.subList(fromIndex = 0, toIndex = i + 1).sortedWith(c)
            assertEquals(l1, l2)
            map = newMap
            assertTrue(map.invariantHolds)
        }
        for (i in 0 until l) {
            val pair = algorithmsPair[i]
            val newMap = map.remove(pair.first)
            val l1 = newMap.bindings.toArrayList().sortedWith(c)
            val l2 = algorithmsPair.subList(fromIndex = i + 1, toIndex = l).sortedWith(c)
            assertEquals(l1, l2)
            map = newMap
            assertTrue(map.invariantHolds)
        }
        assertEquals(emptyMap, map)
    }

    @Test
    fun forEachTest() {
        assertEquals(Unit, emptyMap.forEach { _, _ -> Unit })
        assertEquals(Unit, singletonMap.forEach { _, _ -> Unit })
    }

    @Test
    fun existsTest() {
        // empty map
        assertEquals(false, emptyMap.exists(predicate = always))
        assertEquals(false, emptyMap.exists(predicate = eq))
        assertEquals(false, emptyMap.exists(predicate = nEq))
        // singleton map
        assertEquals(true, singletonMap.exists(predicate = always))
        assertEquals(true, singletonMap.exists(predicate = eq))
        assertEquals(false, singletonMap.exists(predicate = nEq))
    }

    @Test
    fun forAllTest() {
        // empty map
        assertEquals(true, emptyMap.all(predicate = never))
        assertEquals(true, emptyMap.all(predicate = eq))
        assertEquals(true, emptyMap.all(predicate = nEq))
        // singleton map
        assertEquals(false, singletonMap.all(predicate = never))
        assertEquals(true, singletonMap.all(predicate = eq))
        assertEquals(false, singletonMap.all(predicate = nEq))
    }

    @Test
    fun filterTest() {
        // empty map
        assertEquals(emptyMap, emptyMap.filter(predicate = always))
        assertEquals(emptyMap, emptyMap.filter(predicate = never))
        assertEquals(emptyMap, emptyMap.filter(predicate = eq))
        assertEquals(emptyMap, emptyMap.filter(predicate = nEq))
        // singleton map
        assertEquals(singletonMap, singletonMap.filter(predicate = always))
        assertEquals(emptyMap, singletonMap.filter(predicate = never))
        assertEquals(singletonMap, singletonMap.filter(predicate = eq))
        assertEquals(emptyMap, singletonMap.filter(predicate = nEq))
    }

    @Test
    fun partitionTest() {
        // empty map
        assertEquals(emptyMap to emptyMap, emptyMap.partition(predicate = always))
        assertEquals(emptyMap to emptyMap, emptyMap.partition(predicate = never))
        assertEquals(emptyMap to emptyMap, emptyMap.partition(predicate = eq))
        assertEquals(emptyMap to emptyMap, emptyMap.partition(predicate = nEq))
        // singleton map
        assertEquals(singletonMap to emptyMap, singletonMap.partition(predicate = always))
        assertEquals(emptyMap to singletonMap, singletonMap.partition(predicate = never))
        assertEquals(singletonMap to emptyMap, singletonMap.partition(predicate = eq))
        assertEquals(emptyMap to singletonMap, singletonMap.partition(predicate = nEq))
    }

    @Test
    fun peekTest() {
        assertNull(emptyMap.peek())
        assertNotNull(singletonMap.peek())
        assertNotNull(FpMap.create(simplePair, simplePair, "d" to 4).peek())
    }

    @Test
    fun mapTest() {
        // empty map
        assertEquals(emptyMap, emptyMap.mapByKey { simplePair.first })
        assertEquals(emptyMap, emptyMap.mapByValue { simplePair.second })
        assertEquals(emptyMap, emptyMap.mapByKeyValuePair { _, _ -> simplePair })
        // singleton map
        assertEquals(singletonMap, singletonMap.mapByKey { simplePair.first })
        assertEquals(singletonMap, singletonMap.mapByValue { simplePair.second })
        assertEquals(singletonMap, singletonMap.mapByKeyValuePair { _, _ -> simplePair })
        assertEquals(FpMap.create("ddd" to 0), singletonMap.mapByKey { "ddd" })
        assertEquals(FpMap.create("hi" to 1), singletonMap.mapByValue { 1 })
        assertEquals(FpMap.create("d" to 1), singletonMap.mapByKeyValuePair { _, _ -> "d" to 1 })
    }

}
