package org.sampl.runtime

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.runtime.RuntimeProcessor.toAnnotatedMethods

/**
 * [RuntimeProcessorTest] tests whether the [RuntimeProcessor] works correctly.
 */
class RuntimeProcessorTest {

    /**
     * [testCorrectness] tests whether running [RuntimeProcessor] on [lib] will produce
     * [expectedOutput].
     */
    private fun testCorrectness(lib: RuntimeLibrary, expectedOutput: List<Pair<String, TypeExpr>>) {
        assertEquals(expectedOutput, lib.toAnnotatedMethods())
    }

    /**
     * [emptyTest] tests whether an empty [RuntimeLibrary] corresponds to an empty list.
     */
    @Test
    fun emptyTest() {
        testCorrectness(lib = RuntimeLibrary.EmptyInstance, expectedOutput = emptyList())
    }

    /**
     * [RuntimeLibraryWithOnePrivateFunction] is a runtime library with only private function.
     */
    private object RuntimeLibraryWithOnePrivateFunction : RuntimeLibrary {
        @RuntimeFunction
        @JvmStatic
        private fun abc(): Unit = Unit
    }

    /**
     * [RuntimeLibraryWithOnePrivateFunction] is a runtime library with only private function.
     */
    private object RuntimeLibraryWithOnePublicFunction : RuntimeLibrary {
        @RuntimeFunction
        @JvmStatic
        fun abc(): Unit = Unit
    }

    /**
     * [onePrivateFunctionTest] tests whether a [RuntimeLibrary] with only one private function
     * corresponds to an empty list.
     */
    @Test
    fun onePrivateFunctionTest() {
        testCorrectness(lib = RuntimeLibraryWithOnePrivateFunction, expectedOutput = emptyList())
    }

    /**
     * [onePublicFunctionTest] tests whether a [RuntimeLibrary] with only one public function
     * corresponds to a singleton list with that function's info.
     */
    @Test
    fun onePublicFunctionTest() {
        testCorrectness(lib = RuntimeLibraryWithOnePublicFunction, expectedOutput = listOf(
                "abc" to TypeExpr.Function(emptyList(), unitTypeExpr)
        ))
    }

    /**
     * [RuntimeLibraryWithNoAnnotation] is a runtime library with no annotated function.
     */
    private object RuntimeLibraryWithNoAnnotation : RuntimeLibrary {
        @JvmStatic
        fun abc(): Unit = Unit
    }

    /**
     * [onePublicFunctionTest] tests whether a [RuntimeLibrary] with no annotated functions
     * corresponds to an empty list.
     */
    @Test
    fun noAnnotationEmptyTest() {
        testCorrectness(lib = RuntimeLibraryWithNoAnnotation, expectedOutput = emptyList())
    }

}
