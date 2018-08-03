package org.sampl.blackbox

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import org.sampl.FullCompiler
import org.sampl.PLInterpreter
import org.sampl.codegen.PrettyPrinter
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.exceptions.PLException
import org.sampl.util.createRawProgramFromSource

/**
 * [TestCase] defines a set of supported types of test case.
 *
 * @property name name of the test case.
 * @property sourceCode the source code to run the test on.
 */
sealed class TestCase(val name: String, val sourceCode: String) {

    /**
     * [failTest] reports the failure of the test with [reason].
     */
    protected fun failTest(reason: String): Unit = fail("Test $name failed because:\n$reason")

    /**
     * [run] runs the test case to see if it works.
     * It should throw an exception in case it fails.
     */
    abstract fun run()

    /**
     * [CompileWithoutError] tests whether the source code compiles without error.
     */
    class CompileWithoutError(name: String, sourceCode: String) :
            TestCase(name = name, sourceCode = sourceCode) {

        /**
         * @see TestCase.run
         */
        override fun run(): Unit =
                try {
                    FullCompiler.compile(code = sourceCode)
                } catch (e: Throwable) {
                    failTest(reason = e.message ?: "Unknown")
                }

    }

    /**
     * [DoesNotCompile] tests whether the source code does not compile as expected.
     */
    class DoesNotCompile(name: String, sourceCode: String) :
            TestCase(name = name, sourceCode = sourceCode) {

        /**
         * @see TestCase.run
         */
        override fun run(): Unit =
                try {
                    FullCompiler.compile(code = sourceCode)
                    failTest(reason = "It should not compile!")
                } catch (e: Throwable) {
                    // Expected behavior!
                }

    }

    /**
     * [InterpretationWithExpectedResult] tests whether the interpretation gives the expected
     * result.
     */
    class InterpretationWithExpectedResult(
            name: String, sourceCode: String, private val expectedResult: String
    ) : TestCase(name = name, sourceCode = sourceCode) {

        /**
         * @see TestCase.run
         */
        override fun run(): Unit =
                assertEquals(expectedResult, PLInterpreter.interpret(code = sourceCode).toString())

    }

    /**
     * [InterpretationWithExpectedError] tests whether the interpretation gives the expected error.
     */
    class InterpretationWithExpectedError(
            name: String, sourceCode: String, private val expectedError: String
    ) : TestCase(name = name, sourceCode = sourceCode) {

        /**
         * @see TestCase.run
         */
        override fun run(): Unit =
                try {
                    PLInterpreter.interpret(code = sourceCode)
                    failTest(reason = "Does not give the expected error $expectedError.")
                } catch (e: PLException) {
                    assertEquals(e.m, expectedError)
                }

    }

    /**
     * [Pipeline] tests whether the given [sourceCode] can pass the pipeline of
     * type-checking -> pretty-print -> compile.
     */
    class Pipeline(name: String, sourceCode: String) :
            TestCase(name = name, sourceCode = sourceCode) {

        /**
         * @see TestCase.run
         */
        override fun run() {
            val firstTypeCheck = createRawProgramFromSource(code = sourceCode).typeCheck()
            val prettyPrintedCode = PrettyPrinter.prettyPrint(node = firstTypeCheck)
            val secondTypeCheck = createRawProgramFromSource(prettyPrintedCode).typeCheck()
            assertEquals(firstTypeCheck, secondTypeCheck)
            ToKotlinCompiler.compile(node = secondTypeCheck)
        }

    }

}
