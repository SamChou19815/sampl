package org.sampl

import junit.framework.TestCase.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.sampl.codegen.IdtQueue
import org.sampl.codegen.IdtStrategy
import org.sampl.codegen.PrettyPrinter
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.util.createClassFromSource
import org.sampl.util.writeToFile

/**
 * [SimpleTest] contains some simple programs to demonstrate the working status of the system.
 */
class SimpleTest {

    /**
     * [propositionsAreTypesProofsAreProgram] is a program that illustrates the concept of
     * 'Propositions Are Types, Proofs Are Programs'.
     */
    private val propositionsAreTypesProofsAreProgram: String = """
    class TestingProgram {
        let trueVar = ()
        let implication = function(a: String) -> 5
        fun <A, B> modusPonens(f: (A) -> B, v: A): B = f(v)
        // Function Application
        fun constant5Impl1(v: String): Int = implication(v)
        fun constant5Impl2(v: String): Int = modusPonens<String, Int>(implication, v)
        fun applyWithString(): Int = constant5Impl2("hi")
        fun add(a: Int, b: Int): Int = a + b
        fun add1(b: Int): Int = add(1)(b)
        fun main(): Unit = ()
        // Classes
        class And<A, B>(a: A, b: B)
        class Or<A, B>(
          First of A | Second of B
        )
        class Empty
    }
    """.trimIndent()

    /**
     * [standardRuntimeSignatureProgram] is a trivial implementation of the standard runtime just
     * to test its signature is OK.
     */
    private val standardRuntimeSignatureProgram: String = """
    class Runtime {
        fun printInt(value: String): Unit = ()
        fun printFloat(value: Float): Unit = ()
        fun printBool(value: Float): Unit = ()
        fun printChar(value: Float): Unit = ()
        fun printString(value: Float): Unit = ()
        fun <T> printObject(value: T): Unit = ()
        fun println(): Unit = ()
        fun printlnInt(value: String): Unit = ()
        fun printlnFloat(value: Float): Unit = ()
        fun printlnBool(value: Float): Unit = ()
        fun printlnChar(value: Float): Unit = ()
        fun printlnString(value: Float): Unit = ()
        fun readLine(): String = ""
        fun floatToInt(value: Float): Int = 0
        fun stringToInt(value: String): Int = if (true) then 0 else throw<Int> "NOT_CONVERTIBLE"
        fun intToFloat(value: Int): Float = 0.0
        fun stringToFloat(value: String): Float = 0.0
        fun intToString(value: Int): String = ""
        fun floatToString(value: Float): String = ""
        fun boolToString(value: Bool): String = ""
        fun charToString(value: Char): String = ""
        fun <T> objectToString(value: T): String = ""
        fun getChar (index: Int, s: String): Char = 'c'
        fun getSubstring (from: Int, to: Int, s: String): Char = 'c'
    }
    """.trimIndent()

    /**
     * [runInSteps] simply runs some code in [program] in compiler's steps to show that the system
     * kinds of works in each step.
     * The output is written in Program + outputId.kt
     */
    private fun runInSteps(program: String, outputId: Int) {
        val firstTypeCheck = createClassFromSource(program).typeCheck()
        val prettyPrintedCode = PrettyPrinter.prettyPrint(node = firstTypeCheck)
        // println(prettyPrintedCode)
        val secondTypeCheck = createClassFromSource(prettyPrintedCode).typeCheck()
        assertEquals(firstTypeCheck, secondTypeCheck)
        val kotlinCode = IdtQueue(strategy = IdtStrategy.FOUR_SPACES)
                .apply { ToKotlinCompiler.compile(node = secondTypeCheck) }
                .toIndentedCode()
        writeToFile(filename = "./src/test/resources/Program$outputId.kt", content = kotlinCode)
    }

    /**
     * [runInSteps] simply runs some code in compiler's steps to show that the system kinds of
     * works in each step.
     */
    @Test
    fun runInSteps() {
        runInSteps(program = propositionsAreTypesProofsAreProgram, outputId = 1)
        runInSteps(program = "class Empty", outputId = 2)
        runInSteps(program = standardRuntimeSignatureProgram, outputId = 3)
    }

    /**
     * [compileSimple] tests the compiler pipe line as a whole on a simple program.
     */
    @Test
    fun compileSimple() {
        PLCompiler.compileFromSource(code = propositionsAreTypesProofsAreProgram)
    }

}
