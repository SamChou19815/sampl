package org.sampl

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.sampl.codegen.PrettyPrinter
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.runtime.RuntimeLibrary
import org.sampl.util.AntlrUtil
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
        let implication = { (a: String) -> 5 }
        fun <A, B> modusPonens(f: (A) -> B, v: A): B = f(v)
        // Classes
        class And<A, B>(a: A, b: B)
        class Or<A, B>(
          First of A | Second of B
        )
        class Empty
    }
    """.trimIndent()

    /**
     * [multipleFeaturesProgram] is a program that demonstrate various aspect of this programming
     * language.
     */
    private val multipleFeaturesProgram: String = """
    class MultipleFeatures {
        let implication = { (a: String) -> 5 }
        fun <A, B> modusPonens(f: (A) -> B, v: A): B = f(v)
        // Function Application
        fun constant5Impl1(v: String): Int = implication(v)
        fun constant5Impl2(v: String): Int = modusPonens<String, Int>(implication, v)
        fun applyWithString(): Int = constant5Impl2("hi")
        // Curring
        fun add(a: Int, b: Int): Int = a + b
        fun add1(b: Int): Int = add(1)(b)
        // Main
        fun main(): Unit = ()
    }
    """.trimIndent()

    /**
     * [standardRuntimeSignatureProgram] is a trivial implementation of the standard runtime just
     * to test its signature is OK.
     */
    private val standardRuntimeSignatureProgram: String = """
    class Runtime {
        fun printInt(value: Int): Unit = ()
        fun printFloat(value: Float): Unit = ()
        fun printBool(value: Bool): Unit = ()
        fun printChar(value: Char): Unit = ()
        fun printString(value: String): Unit = ()
        fun println(): Unit = ()
        fun printlnInt(value: Int): Unit = ()
        fun printlnFloat(value: Float): Unit = ()
        fun printlnBool(value: Bool): Unit = ()
        fun printlnChar(value: Char): Unit = ()
        fun printlnString(value: String): Unit = ()
        fun readLine(): String = ""
        fun floatToInt(value: Float): Int = 0
        fun stringToInt(value: String): Int = if (true) then 0 else throw<Int> "NOT_CONVERTIBLE"
        fun intToFloat(value: Int): Float = 0.0
        fun stringToFloat(value: String): Float = 0.0
        fun intToString(value: Int): String = ""
        fun floatToString(value: Float): String = ""
        fun boolToString(value: Bool): String = ""
        fun charToString(value: Char): String = ""
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
        val firstTypeCheck = AntlrUtil.createClassFromSource(program).typeCheck()
        val prettyPrintedCode = PrettyPrinter.prettyPrint(node = firstTypeCheck)
        // println(prettyPrintedCode)
        val secondTypeCheck = AntlrUtil.createClassFromSource(prettyPrintedCode).typeCheck()
        assertEquals(firstTypeCheck, secondTypeCheck)
        val kotlinCode = ToKotlinCompiler.compile(node = secondTypeCheck)
        writeToFile(filename = "./src/test/resources/Program$outputId.kt", content = kotlinCode)
    }

    /**
     * [runInSteps] simply runs some code in compiler's steps to show that the system kinds of
     * works in each step.
     */
    @Test
    fun runInSteps() {
        runInSteps(program = propositionsAreTypesProofsAreProgram, outputId = 1)
        runInSteps(program = multipleFeaturesProgram, outputId = 2)
        runInSteps(program = standardRuntimeSignatureProgram, outputId = 3)
    }

    /**
     * [compileSimple] tests the compiler pipe line as a whole on a simple program.
     */
    @Test
    fun compileSimple() {
        FullCompiler.compile(
                code = multipleFeaturesProgram,
                providedRuntimeLibrary = RuntimeLibrary.EmptyInstance
        )
    }

}
