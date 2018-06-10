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
 * [printInt] prints [value] to the console without a new line.
 */
fun printInt(value: String): Unit = print(value)

/* 
 * [printFloat] prints [value] to the console without a new line.
 */
fun printFloat(value: Float): Unit = print(value)

/* 
 * [printBool] prints [value] to the console without a new line.
 */
fun printBool(value: Float): Unit = print(value)

/* 
 * [printChar] prints [value] to the console without a new line.
 */
fun printChar(value: Float): Unit = print(value)

/* 
 * [printString] prints [value] to the console without a new line.
 */
fun printString(value: Float): Unit = print(value)

/* 
 * [printObject] prints [value] to the console without a new line.
 */
fun <T> printObject(value: T): Unit = print(value)

/* 
 * [println] prints an empty line to the console.
 */
fun println(): Unit = println()

/* 
 * [printlnInt] prints [value] to the console with a new line.
 */
fun printlnInt(value: String): Unit = println(value)

/* 
 * [printlnFloat] prints [value] to the console with a new line.
 */
fun printlnFloat(value: Float): Unit = println(value)

/* 
 * [printlnBool] prints [value] to the console with a new line.
 */
fun printlnBool(value: Float): Unit = println(value)

/* 
 * [printlnChar] prints [value] to the console with a new line.
 */
fun printlnChar(value: Float): Unit = println(value)

/* 
 * [printlnString] prints [value] to the console with a new line.
 */
fun printlnString(value: Float): Unit = println(value)

/*
 * ------------------------------------------------------------
 * Part 2: Readers
 * ------------------------------------------------------------
 */

/* 
 * Reads a line from the console. Blocks until there is a line.
 */
fun readLine(): String = readLine()!!

/*
 * ------------------------------------------------------------
 * Part 3: Primitive Type Converters
 * ------------------------------------------------------------
 */

/* 
 * Convert [value] to int. It will always succeed, but with some precision loss.
 */
fun floatToInt(value: Float): Int = value.toLong()

/* 
 * Convert [value] to int. Throws "NOT_CONVERTIBLE" if failed.
 */
fun stringToInt(value: String): Int =
        value.toIntOrNull() ?: throw PLException("NOT_CONVERTIBLE")

/* 
 * Convert [value] to float. It will always succeed, but with some precision loss.
 */
fun intToFloat(value: Int): Float = value.toDouble()

/* 
 * Convert [value] to float. Throws "NOT_CONVERTIBLE" if failed.
 */
fun stringToFloat(value: String): Float = 
        value.toDoubleOrNull() ?: throw PLException("NOT_CONVERTIBLE")

/* 
 * Convert [value] to string. It will always succeed.
 */
fun intToString(value: Int): String = value.toString()

/* 
 * Convert [value] to string. It will always succeed.
 */
fun floatToString(value: Float): String = value.toString()

/* 
 * Convert [value] to string. It will always succeed.
 */
fun boolToString(value: Bool): String = value.toString()

/* 
 * Convert [value] to string. It will always succeed.
 */
fun charToString(value: Char): String = value.toString()

/* 
 * Convert [value] to string. It will always succeed.
 */
fun <T> objectToString(value: T): String = value.toString()

/*
 * ------------------------------------------------------------
 * Part 4: String Functions
 * ------------------------------------------------------------
 */

/* 
 * Returns char at [index] of [s]. Throws "OUT_OF_BOUND" if [index] is out of bound.
 */
fun getChar (index: Int, s: String): Char = 
        try { s[index] } catch (e: IndexOutOfBoundsException) {
            throw throw PLException("OUT_OF_BOUND")
        }

/* 
 * Returns substring from [from] (inclusive) to [to] (exclusive) [s]. Throws "OUT_OF_BOUND" if 
 * [from] or [to] is an out of bound index.
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
