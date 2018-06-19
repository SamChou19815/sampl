package org.sampl.runtime

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeInfo
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.exceptions.DisallowedRuntimeFunctionError

/**
 * [RuntimeProcessorTest] tests whether the Runtime Processor works correctly.
 */
class RuntimeProcessorTest {

    /**
     * [testCorrectness] tests whether running [RuntimeProcessor] on [lib] will produce
     * [expectedOutput].
     * It has a parameter [allowGenerics] which defaults to false. It can be used to control whether
     * to allow generics in runtime library.
     */
    private fun testCorrectness(lib: RuntimeLibrary, expectedOutput: List<Pair<String, TypeInfo>>,
                                allowGenerics: Boolean = false) {
        assertEquals(expectedOutput, lib.toAnnotatedFunctions(allowGenerics = allowGenerics))
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
        fun ab(): Unit = Unit
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
                "ab" to TypeExpr.Function(emptyList(), unitTypeExpr).let { TypeInfo(typeExpr = it) }
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

    /**
     * [SimpleLibraryWithGenerics] is a quick demo to see how generics works in the processor.
     */
    private object SimpleLibraryWithGenerics : RuntimeLibrary {
        @RuntimeFunction
        @JvmStatic
        fun <T : Any> objectToString(obj: T): String = obj.toString()

    }

    /**
     * [simpleLibraryWithGenericsDisallowedTest] tests whether the processor can correctly deal
     * with generics information when it's not allowed.
     */
    @Test(expected = DisallowedRuntimeFunctionError::class)
    fun simpleLibraryWithGenericsDisallowedTest() {
        SimpleLibraryWithGenerics.toAnnotatedFunctions()
    }

    /**
     * [simpleLibraryWithGenericsAllowedTest] tests whether the processor can correctly deal with
     * simple generics information when allowed to do so.
     */
    @Test
    fun simpleLibraryWithGenericsAllowedTest() {
        testCorrectness(lib = SimpleLibraryWithGenerics, expectedOutput = listOf(
                "objectToString" to TypeInfo(
                        typeExpr = TypeExpr.Function(
                                argumentTypes = listOf(TypeExpr.Identifier(type = "T")),
                                returnType = stringTypeExpr
                        ),
                        genericsInfo = listOf("T")
                )
        ), allowGenerics = true)
    }

    /**
     * [primitiveRuntimeLibraryIsGoodTest] ensures the primitive runtime library is good to use.
     */
    @Test
    fun primitiveRuntimeLibraryIsGoodTest() {
        PrimitiveRuntimeLibrary.toAnnotatedFunctions(allowGenerics = true)
    }

}
