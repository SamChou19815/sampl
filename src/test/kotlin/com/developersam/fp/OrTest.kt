package com.developersam.fp

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * [FpListTest] contains a collection of tests for or.
 */
class OrTest {

    /**
     * [getString] returns the string in [or] or the string form of the number.
     */
    private fun getString(or: Or<String, Int>): String =
            when (or) {
                is Or.First<String> -> or.data
                is Or.Second<Int> -> or.data.toString()
            }

    /**
     * [patternMatchingTest] tests the pattern matching on or.
     */
    @Test
    fun patternMatchingTest() {
        val f = "hi"
        val s = 0
        val firstOr = Or.First(f)
        val secondOr = Or.Second(s)
        assertEquals("hi", getString(or = firstOr))
        assertEquals("0", getString(or = secondOr))
    }

}
