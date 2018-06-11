package org.sampl.runtime

import org.sampl.ast.type.TypeInfo
import org.sampl.exceptions.PLException

/**
 * [PrimitiveRuntimeLibrary] contains a collection of methods for primitive runtime functions.
 */
object PrimitiveRuntimeLibrary : RuntimeLibrary {

    /*
     * ------------------------------------------------------------
     * Part 0: Information about itself (not part of primitive lib)
     * ------------------------------------------------------------
     */

    /**
     * [annotatedFunctions] contains a list of annotated functions.
     */
    @JvmField
    val annotatedFunctions: List<Pair<String, TypeInfo>> =
            toAnnotatedFunctions(allowGenerics = true)

    /*
     * ------------------------------------------------------------
     * Part 1: Printers
     * ------------------------------------------------------------
     */

    /**
     * [printInt] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printInt(value: Long): Unit = print(value)

    /**
     * [printFloat] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printFloat(value: Double): Unit = print(value)

    /**
     * [printBool] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printBool(value: Boolean): Unit = print(value)

    /**
     * [printChar] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printChar(value: Char): Unit = print(value)

    /**
     * [printString] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printString(value: String): Unit = print(value)

    /**
     * [printObject] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun <T : Any> printObject(value: T): Unit = println(value)

    /**
     * [println] prints an empty line to the standard output.
     */
    @RuntimeFunction
    @JvmStatic
    fun println(): Unit = println()

    /**
     * [printlnInt] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printlnInt(value: Long): Unit = println(value)

    /**
     * [printlnFloat] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printlnFloat(value: Double): Unit = println(value)

    /**
     * [printlnBool] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printlnBool(value: Boolean): Unit = println(value)

    /**
     * [printlnChar] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printlnChar(value: Char): Unit = println(value)

    /**
     * [printlnString] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun printlnString(value: String): Unit = println(value)

    /**
     * [printlnObject] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    @JvmStatic
    fun <T : Any> printlnObject(value: T): Unit = println(value)

    /*
     * ------------------------------------------------------------
     * Part 2: Readers
     * ------------------------------------------------------------
     */

    /**
     * [readLine] reads a line from the standard input and returns the line.
     * Blocks until there is a line.
     */
    @RuntimeFunction
    @JvmStatic
    fun readLine(): String = kotlin.io.readLine()!!

    /*
     * ------------------------------------------------------------
     * Part 3: Primitive Type Converters
     * ------------------------------------------------------------
     */

    /**
     * [floatToInt] converts [value] to int. It will always succeed, but with some precision loss.
     */
    @RuntimeFunction
    @JvmStatic
    fun floatToInt(value: Double): Long = value.toLong()

    /**
     * [stringToInt] converts [value] to int. Throws "NOT_CONVERTIBLE" if failed.
     */
    @RuntimeFunction
    @JvmStatic
    fun stringToInt(value: String): Long =
            value.toLongOrNull() ?: throw PLException("NOT_CONVERTIBLE")

    /**
     * [intToFloat] converts [value] to float. It will always succeed, but with some precision loss.
     */
    @RuntimeFunction
    @JvmStatic
    fun intToFloat(value: Long): Double = value.toDouble()

    /**
     * [stringToFloat] converts [value] to float. Throws "NOT_CONVERTIBLE" if failed.
     */
    @RuntimeFunction
    @JvmStatic
    fun stringToFloat(value: String): Double =
            value.toDoubleOrNull() ?: throw PLException("NOT_CONVERTIBLE")

    /**
     * [intToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    @JvmStatic
    fun intToString(value: Long): String = value.toString()

    /**
     * [floatToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    @JvmStatic
    fun floatToString(value: Double): String = value.toString()

    /**
     * [boolToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    @JvmStatic
    fun boolToString(value: Boolean): String = value.toString()

    /**
     * [charToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    @JvmStatic
    fun charToString(value: Char): String = value.toString()

    /**
     * [objectToString] converts [value] to string. It will always succeed.
     * Do not use this function for other primitive types.
     */
    @RuntimeFunction
    @JvmStatic
    fun <T : Any> objectToString(value: T): String = value.toString()

    /*
     * ------------------------------------------------------------
     * Part 4: String Functions
     * ------------------------------------------------------------
     */

    /**
     * [getChar] returns char at [index] of [s]. Throws "OUT_OF_BOUND" if [index] is out of bound.
     */
    @RuntimeFunction
    @JvmStatic
    fun getChar(index: Long, s: String): Char =
            try {
                s[index.toInt()]
            } catch (e: IndexOutOfBoundsException) {
                throw throw PLException("OUT_OF_BOUND")
            }

    /**
     * [getSubstring] returns substring from [from] (inclusive) to [to] (exclusive) [s]. Throws
     * "OUT_OF_BOUND" if [from] or [to] is an out of bound index or [from] is greater than [to].
     */
    @RuntimeFunction
    @JvmStatic
    fun getSubstring(from: Long, to: Long, s: String): String =
            try {
                s.substring(from.toInt(), to.toInt())
            } catch (e: IndexOutOfBoundsException) {
                throw throw PLException("OUT_OF_BOUND")
            }

}
