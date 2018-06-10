package org.sampl.benchmark

import org.junit.Test
import java.util.Random

/**
 * [ReflectionBenchmarkTest] tests the performance penalty of using reflection.
 */
class ReflectionBenchmarkTest {

    /**
     * [repeats] defines the number of times of repeat.
     */
    private val repeats = 1 shl 20

    /**
     * [Tester] is used to test the performance of reflection.
     */
    private object Tester {

        /**
         * [random] is the random number generator used.
         */
        private val random = Random()

        /**
         * [test] is a trivial method.
         */
        fun test() {
            random.nextInt() + random.nextInt()
        }

        /**
         * [reflectionTest] is the same as [test] but invoked using reflection.
         */
        fun reflectionTest() {
            javaClass.getMethod("test").invoke(this)
        }

    }

    @Test
    fun simpleInvocationTest() {
        repeat(times = repeats) { Tester.test() }
    }

    @Test
    fun reflectionInvocationTest() {
        repeat(times = repeats) { Tester.reflectionTest() }
    }

}
