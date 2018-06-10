# SAMPL Language Specification

## Grammar Specification

You can read the spec for grammar by reading the code at

- [PL.g4](./src/main/antlr/PL.g4)
- [PLLexerPart.g4](./src/main/antlr/PLLexerPart.g4)

## Runtime Specification

The signature (in SAMPL) and implementation (in Kotlin) of the provided runtime functions are given 
below:

```kotlin
/*
 * ------------------------------------------------------------
 * Part 1: Printers
 * ------------------------------------------------------------
 */

/* 
 * [printInt] prints [value] to the standard output without a new line.
 */
fun printInt(value: String): Unit = print(value)

/* 
 * [printFloat] prints [value] to the standard output without a new line.
 */
fun printFloat(value: Float): Unit = print(value)

/* 
 * [printBool] prints [value] to the standard output without a new line.
 */
fun printBool(value: Bool): Unit = print(value)

/* 
 * [printChar] prints [value] to the standard output without a new line.
 */
fun printChar(value: Char): Unit = print(value)

/* 
 * [printString] prints [value] to the standard output without a new line.
 */
fun printString(value: String): Unit = print(value)

/* 
 * [printObject] prints [value] to the standard output without a new line.
 */
fun <T> printObject(value: T): Unit = print(value)

/* 
 * [println] prints an empty line to the standard output.
 */
fun println(): Unit = println()

/* 
 * [printlnInt] prints [value] to the standard output with a new line.
 */
fun printlnInt(value: Int): Unit = println(value)

/* 
 * [printlnFloat] prints [value] to the standard output with a new line.
 */
fun printlnFloat(value: Float): Unit = println(value)

/* 
 * [printlnBool] prints [value] to the standard output with a new line.
 */
fun printlnBool(value: Bool): Unit = println(value)

/* 
 * [printlnChar] prints [value] to the standard output with a new line.
 */
fun printlnChar(value: Char): Unit = println(value)

/* 
 * [printlnString] prints [value] to the standard output with a new line.
 */
fun printlnString(value: String): Unit = println(value)

/*
 * ------------------------------------------------------------
 * Part 2: Readers
 * ------------------------------------------------------------
 */

/* 
 * [readLine] reads a line from the standard input and returns the line. 
 * Blocks until there is a line.
 */
fun readLine(): String = readLine()!!

/*
 * ------------------------------------------------------------
 * Part 3: Primitive Type Converters
 * ------------------------------------------------------------
 */

/* 
 * [floatToInt] converts [value] to int. It will always succeed, but with some precision loss.
 */
fun floatToInt(value: Float): Int = value.toLong()

/* 
 * [stringToInt] converts [value] to int. Throws "NOT_CONVERTIBLE" if failed.
 */
fun stringToInt(value: String): Int =
        value.toIntOrNull() ?: throw PLException("NOT_CONVERTIBLE")

/* 
 * [intToFloat] converts [value] to float. It will always succeed, but with some precision loss.
 */
fun intToFloat(value: Int): Float = value.toDouble()

/* 
 * [stringToFloat] converts [value] to float. Throws "NOT_CONVERTIBLE" if failed.
 */
fun stringToFloat(value: String): Float = 
        value.toDoubleOrNull() ?: throw PLException("NOT_CONVERTIBLE")

/* 
 * [intToString] converts [value] to string. It will always succeed.
 */
fun intToString(value: Int): String = value.toString()

/* 
 * [floatToString] converts [value] to string. It will always succeed.
 */
fun floatToString(value: Float): String = value.toString()

/* 
 * [boolToString] converts [value] to string. It will always succeed.
 */
fun boolToString(value: Bool): String = value.toString()

/* 
 * [charToString] converts [value] to string. It will always succeed.
 */
fun charToString(value: Char): String = value.toString()

/* 
 * [objectToString] converts [value] to string. It will always succeed.
 */
fun <T> objectToString(value: T): String = value.toString()

/*
 * ------------------------------------------------------------
 * Part 4: String Functions
 * ------------------------------------------------------------
 */

/* 
 * [getChar] returns char at [index] of [s]. Throws "OUT_OF_BOUND" if [index] is out of bound.
 */
fun getChar (index: Int, s: String): Char = 
        try { s[index] } catch (e: IndexOutOfBoundsException) {
            throw throw PLException("OUT_OF_BOUND")
        }

/* 
 * [getSubstring] returns substring from [from] (inclusive) to [to] (exclusive) [s]. Throws 
 * "OUT_OF_BOUND" if [from] or [to] is an out of bound index or [from] is greater than [to].
 */
fun getSubstring (from: Int, to: Int, s: String): Char = 
        try { s.substring(from, to) } catch (e: IndexOutOfBoundsException) {
            throw throw PLException("OUT_OF_BOUND") 
        }

```

## Type Checking Specification

### Scope and Mutually Recursive Problem

Lexical scope is used for type checking, so cyclic dependencies between different classes is 
strictly prohibited. Within each class, type declaration can be recursive. Currently, even this is 
allowed: `class BadExample(value: BadExample)` Classes are all public and static. Types are public
but its definitions can only be used within the scope of the class.

Constants of a class are static. They must appear first in class members declarations and cannot be
mutually recursive with any other member in the entire program. All functions within the class are 
treated as they are all mutually recursive.

As stated before, nested classes cannot be mutually recursive.

### Type Inference and Its Current Limitation

Currently, all parameters in a function must be annotated by their types. For functions as a class 
member, their return types must be annotated; for functions as a value, their return types must not
be annotated. All variables do not have type annotation. The type inference algorithm will 
automatically figure that out. 

(In later versions, we may add support for optional type annotation and respect those annotations.)

### Type Checking Rules

`TODO`

## Evaluation Specification

`TODO`
