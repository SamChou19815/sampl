package org.sampl.runtime

import org.sampl.exceptions.PLException

/**
 * [PrimitiveRuntimeLibrary] contains a collection of methods for primitive runtime functions.
 */
object PrimitiveRuntimeLibrary : RuntimeLibrary {

    /*
     * ------------------------------------------------------------
     * Part 1: Printers
     * ------------------------------------------------------------
     */

    /**
     * [printInt] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    fun printInt(value: Long): Unit = print(value)

    /**
     * [printFloat] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    fun printFloat(value: Double): Unit = print(value)

    /**
     * [printBool] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    fun printBool(value: Boolean): Unit = print(value)

    /**
     * [printChar] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    fun printChar(value: Char): Unit = print(value)

    /**
     * [printString] prints [value] to the standard output without a new line.
     */
    @RuntimeFunction
    fun printString(value: String): Unit = print(value)

    /**
     * [println] prints an empty line to the standard output.
     */
    @RuntimeFunction
    fun println(): Unit = println()

    /**
     * [printlnInt] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    fun printlnInt(value: Long): Unit = println(value)

    /**
     * [printlnFloat] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    fun printlnFloat(value: Double): Unit = println(value)

    /**
     * [printlnBool] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    fun printlnBool(value: Boolean): Unit = println(value)

    /**
     * [printlnChar] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    fun printlnChar(value: Char): Unit = println(value)

    /**
     * [printlnString] prints [value] to the standard output with a new line.
     */
    @RuntimeFunction
    fun printlnString(value: String): Unit = println(value)

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
    fun floatToInt(value: Double): Long = value.toLong()

    /**
     * [stringToInt] converts [value] to int. Throws "NOT_CONVERTIBLE" if failed.
     */
    @RuntimeFunction
    fun stringToInt(value: String): Long =
            value.toLongOrNull() ?: throw PLException("NOT_CONVERTIBLE")

    /**
     * [intToFloat] converts [value] to float. It will always succeed, but with some precision loss.
     */
    @RuntimeFunction
    fun intToFloat(value: Long): Double = value.toDouble()

    /**
     * [stringToFloat] converts [value] to float. Throws "NOT_CONVERTIBLE" if failed.
     */
    @RuntimeFunction
    fun stringToFloat(value: String): Double =
            value.toDoubleOrNull() ?: throw PLException("NOT_CONVERTIBLE")

    /**
     * [intToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    fun intToString(value: Long): String = value.toString()

    /**
     * [floatToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    fun floatToString(value: Double): String = value.toString()

    /**
     * [boolToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    fun boolToString(value: Boolean): String = value.toString()

    /**
     * [charToString] converts [value] to string. It will always succeed.
     */
    @RuntimeFunction
    fun charToString(value: Char): String = value.toString()

    /*
     * ------------------------------------------------------------
     * Part 4: String Functions
     * ------------------------------------------------------------
     */

    /**
     * [getChar] returns char at [index] of [s]. Throws "OUT_OF_BOUND" if [index] is out of bound.
     */
    @RuntimeFunction
    fun getChar(index: Int, s: String): Char =
            try {
                s[index]
            } catch (e: IndexOutOfBoundsException) {
                throw throw PLException("OUT_OF_BOUND")
            }

    /**
     * [getSubstring] returns substring from [from] (inclusive) to [to] (exclusive) [s]. Throws
     * "OUT_OF_BOUND" if [from] or [to] is an out of bound index or [from] is greater than [to].
     */
    @RuntimeFunction
    fun getSubstring(from: Int, to: Int, s: String): String =
            try {
                s.substring(from, to)
            } catch (e: IndexOutOfBoundsException) {
                throw throw PLException("OUT_OF_BOUND")
            }

}
