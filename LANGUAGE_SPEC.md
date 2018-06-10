# SAMPL Language Specification

## Grammar Specification

You can read the spec for grammar by reading the code at

- [PL.g4](./src/main/antlr/PL.g4)
- [PLLexerPart.g4](./src/main/antlr/PLLexerPart.g4)

## Runtime Specification

The signature of the provided runtime functions are given below:

```
/*
 * ------------------------------------------------------------
 * Part 1: Printers
 * ------------------------------------------------------------
 */

/* 
 * Prints [value] to the console without a new line.
 */
let printInt (value: String): Unit = <fun>

/* 
 * Prints [value] to the console without a new line.
 */
let printFloat (value: Float): Unit = <fun>

/* 
 * Prints [value] to the console without a new line.
 */
let printBool (value: Float): Unit = <fun>

/* 
 * Prints [value] to the console without a new line.
 */
let printChar (value: Float): Unit = <fun>

/* 
 * Prints [value] to the console without a new line.
 */
let printString (value: Float): Unit = <fun>

/* 
 * Prints an empty line to the console.
 */
let println (): Unit = <fun>

/* 
 * Prints [value] to the console with a new line.
 */
let printlnInt (value: String): Unit = <fun>

/* 
 * Prints [value] to the console with a new line.
 */
let printlnFloat (value: Float): Unit = <fun>

/* 
 * Prints [value] to the console with a new line.
 */
let printlnBool (value: Float): Unit = <fun>

/* 
 * Prints [value] to the console with a new line.
 */
let printlnChar (value: Float): Unit = <fun>

/* 
 * Prints [value] to the console with a new line.
 */
let printlnString (value: Float): Unit = <fun>

/*
 * ------------------------------------------------------------
 * Part 2: Readers
 * ------------------------------------------------------------
 */

/* 
 * Reads a line from the console. Blocks until there is a line.
 */
let readLine (): String = <fun>

/*
 * ------------------------------------------------------------
 * Part 3: Primitive Type Converters
 * ------------------------------------------------------------
 */

/* 
 * Convert [value] to int. It will always succeed, but with some precision loss.
 */
let floatToInt(value: Float): Int = <fun>

/* 
 * Convert [value] to int. Throws "NOT_CONVERTIBLE" if failed.
 */
let stringToInt(value: String): Int = <fun>

/* 
 * Convert [value] to float. It will always succeed, but with some precision loss.
 */
let intToFloat(value: Int): Float = <fun>

/* 
 * Convert [value] to float. Throws "NOT_CONVERTIBLE" if failed.
 */
let stringToFloat(value: String): Float = <fun>

/* 
 * Convert [value] to string. It will always succeed.
 */
let intToString(value: Int): String = <fun>

/* 
 * Convert [value] to string. It will always succeed.
 */
let floatToString(value: Float): String = <fun>

/* 
 * Convert [value] to string. It will always succeed.
 */
let boolToString(value: Bool): String = <fun>

/* 
 * Convert [value] to string. It will always succeed.
 */
let charToString(value: Char): String = <fun>

/* 
 * Convert [value] to string. It will always succeed.
 */
let <T> objectToString(value: T): String = <fun>

/*
 * ------------------------------------------------------------
 * Part 4: String Functions
 * ------------------------------------------------------------
 */

/* 
 * Returns char at [index] of [s]. Throws "OUT_OF_BOUND" if [index] is out of bound.
 */
let getChar (index: Int, s: String): Char = <fun>

/* 
 * Returns substring from [from] (inclusive) to [to] (exclusive) [s]. Throws "OUT_OF_BOUND" if 
 * [from] or [to] is an out of bound index.
 */
let getSubstring (from: Int, to: Int, s: String): Char = <fun>
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
