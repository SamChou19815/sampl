package org.sampl

import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.sampl.codegen.PrettyPrinter
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.eval.IntValue
import org.sampl.eval.StringValue
import org.sampl.eval.UnitValue
import org.sampl.util.createRawProgramFromSource
import org.sampl.util.readFromFile
import org.sampl.util.writeToFile

/**
 * [SimpleTest] contains some simple programs to demonstrate the working status of the system.
 */
class SimpleTest {

    /**
     * [propositionsAreTypesProofsArePrograms] is a program that illustrates the concept of
     * 'Propositions Are Types, Proofs Are Programs'.
     */
    private val propositionsAreTypesProofsArePrograms: String = """
        val trueVar = ()
        val implication = { (a: String) -> 5 }
        fun <A, B> modusPonens(f: (A) -> B, v: A): B = f(v)
        // Classes
        class And<A, B>(a: A, b: B)
        class Or<A, B>(First of A | Second of B)
        class Optional<T>(None | Some of T) {
           fun <T> hasValue(v: Optional<T>): Bool =
             match v with
             | None -> false
             | Some _ -> true
        }
        class Empty
    """.trimIndent()

    /**
     * [multipleFeaturesProgram] is a program that demonstrate various aspect of this programming
     * language.
     */
    private val multipleFeaturesProgram: String = """
        val implication = { (a: String) -> 5 }
        fun <A, B> modusPonens(f: (A) -> B, v: A): B = f(v)
        // Function Application
        fun constant5Impl1(v: String): Int = implication(v)
        fun constant5Impl2(v: String): Int = modusPonens(implication, v)
        fun applyWithString(): Int = constant5Impl2("hi")
        // Curring
        fun add1(b: Int): Int = add(1)(b)
        fun add(a: Int, b: Int): Int = a + b
        // Main
        fun main(): Int = add1(41)
    """.trimIndent()

    /**
     * [standardRuntimeSignatureProgram] is a trivial implementation of the standard runtime just
     * to test its signature is OK.
     */
    private val standardRuntimeSignatureProgram: String = """
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
        fun getChar(index: Int, s: String): Char = 'c'
        fun getSubstring(from: Int, to: Int, s: String): Char = 'c'
    """.trimIndent()

    /**
     * [standardHelloWorldProgram] is the standard hello world program.
     */
    private val standardHelloWorldProgram: String =
            """fun main(): Unit = printlnString("Hello World, " ^ "Sam!")"""

    /**
     * [stringHelloWorldProgram] is the hello world program but returns a string.
     */
    private val stringHelloWorldProgram: String =
            """fun main(): String = "Hello World, " ^ "Sam!""""

    /**
     * [intHelloWorldProgram] is the hello world program but returns an int.
     */
    private val intHelloWorldProgram: String = """
        fun main(): Int =
          val a = 32;
          a + 10
    """.trimIndent()

    /**
     * [turingMachineSimulatorProgram] is the program to simulate Turing machines.
     */
    private val turingMachineSimulatorProgram: String =
            readFromFile(filename = "./src/main/resources/TuringMachineSimulator.sampl")!!

    /**
     * [runInSteps] simply runs some code in [program] in compiler's steps to show that the system
     * kinds of works in each step.
     * The output is written in Program + outputId.kt
     */
    private fun runInSteps(program: String, outputId: Int) {
        val firstTypeCheck = createRawProgramFromSource(program).typeCheck()
        val prettyPrintedCode = PrettyPrinter.prettyPrint(node = firstTypeCheck)
        // println(prettyPrintedCode)
        val secondTypeCheck = createRawProgramFromSource(prettyPrintedCode).typeCheck()
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
        runInSteps(program = propositionsAreTypesProofsArePrograms, outputId = 1)
        runInSteps(program = multipleFeaturesProgram, outputId = 2)
        runInSteps(program = standardRuntimeSignatureProgram, outputId = 3)
        runInSteps(program = standardHelloWorldProgram, outputId = 4)
        runInSteps(program = stringHelloWorldProgram, outputId = 5)
        runInSteps(program = intHelloWorldProgram, outputId = 6)
    }

    /**
     * [compileSimple] tests the compiler pipe line as a whole on a simple program.
     */
    @Test
    fun compileSimple(): Unit = FullCompiler.compile(code = turingMachineSimulatorProgram)

    /**
     * [interpretSimple] tests the interpreter pipe line as a whole on a simple program.
     */
    @Test
    fun interpretSimple() {
        assertEquals(UnitValue, PLInterpreter.interpret(propositionsAreTypesProofsArePrograms))
        assertEquals(IntValue(value = 42), PLInterpreter.interpret(multipleFeaturesProgram))
        assertEquals(StringValue(value = "Hello World, Sam!"),
                PLInterpreter.interpret(stringHelloWorldProgram))
        assertEquals(IntValue(value = 42), PLInterpreter.interpret(intHelloWorldProgram))
    }

}
