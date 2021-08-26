package com.developersam.fp

import junit.framework.TestCase.assertEquals
import org.junit.Test

class FpListTest {
    private val emptyList: FpList<String> = FpList.empty()
    private val singletonList: FpList<String> = FpList.singleton(data = "hi")
    private val complexList: FpList<String> = "hello" cons ("world" cons emptyList)

    @Test
    fun createTest() {
        assertEquals(emptyList, FpList.create<String>())
        assertEquals(singletonList, FpList.create("hi"))
        assertEquals(complexList, FpList.create("hello", "world"))
    }

    @Suppress(names = ["UNUSED_PARAMETER"])
    private fun <T> ignore(v: T): Unit = Unit

    @Test(expected = NotFoundError::class)
    fun emptyListHeadTest(): Unit = ignore(v = emptyList.head)

    @Test(expected = NotFoundError::class)
    fun emptyListTailTest(): Unit = ignore(v = emptyList.tail)

    @Test
    fun nonEmptyListHeadTailTest() {
        assertEquals("hi", singletonList.head)
        assertEquals(FpList.Nil, singletonList.tail)
        assertEquals("hello", complexList.head)
        assertEquals(FpList.singleton("world"), complexList.tail)
    }

    @Test
    fun sizeTest() {
        assertEquals(0, emptyList.size)
        assertEquals(1, singletonList.size)
        assertEquals(2, complexList.size)
    }

    @Test
    fun reverseTest() {
        assertEquals(emptyList, emptyList.reverse)
        assertEquals(emptyList, singletonList.reverse)
        assertEquals(FpList.create("world", "hello"), complexList.reverse)
    }

    @Test
    fun containsTest() {
        // empty list
        assertEquals(false, "hi" in emptyList)
        assertEquals(false, "hello" in emptyList)
        assertEquals(false, "world" in emptyList)
        // singleton list
        assertEquals(true, "hi" in singletonList)
        assertEquals(false, "hello" in singletonList)
        assertEquals(false, "world" in singletonList)
        // complex list
        assertEquals(false, "hi" in complexList)
        assertEquals(true, "hello" in complexList)
        assertEquals(true, "world" in complexList)
    }

    @Test
    fun appendTest() {
        assertEquals(singletonList, emptyList.append(singletonList))
        assertEquals(singletonList, singletonList.append(emptyList))
        assertEquals(complexList, emptyList.append(complexList))
        assertEquals(complexList, complexList.append(emptyList))
        assertEquals(FpList.create("hi", "hello", "world"), singletonList.append(complexList))
    }

    @Test
    fun higherOrderFunctionsTest() {
        // empty list
        assertEquals(false, emptyList.exists { true })
        assertEquals(true, emptyList.all { false })
        assertEquals(Unit, emptyList.forEach { })
        assertEquals("", emptyList.fold(initial = "", operation = String::plus))
        assertEquals("", emptyList.foldRight(initial = "", operation = String::plus))
        assertEquals(emptyList, emptyList.map { "Bad!" })
        // singleton list
        assertEquals(false, singletonList.exists { false })
        assertEquals(true, singletonList.exists { it == "hi" })
        assertEquals(false, singletonList.all { false })
        assertEquals(true, singletonList.all { it == "hi" })
        assertEquals(Unit, singletonList.forEach { })
        assertEquals("hi", singletonList.fold(initial = "", operation = String::plus))
        assertEquals("hi", singletonList.foldRight(initial = "", operation = String::plus))
        assertEquals(FpList.singleton("bad"), singletonList.map { "bad" })
        // complex list
        assertEquals(false, complexList.exists { false })
        assertEquals(false, complexList.exists { it == "hi" })
        assertEquals(true, complexList.exists { it == "hello" })
        assertEquals(true, complexList.exists { it == "world" })
        assertEquals(false, complexList.all { false })
        assertEquals(false, complexList.all { it == "hi" })
        assertEquals(true, complexList.all(String::isNotEmpty))
        assertEquals(Unit, complexList.forEach { })
        assertEquals(
                "helloworld", complexList.fold(initial = "", operation = String::plus)
        )
        assertEquals(
                "worldhello", complexList.foldRight(initial = "", operation = String::plus)
        )
        assertEquals(
                FpList.create("HELLO", "WORLD"), complexList.map(transform = String::toUpperCase)
        )
    }

}
