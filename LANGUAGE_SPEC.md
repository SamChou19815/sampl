# SAMPL Language Specification

## Grammar Specification

You can read the spec for grammar by reading the code at

- [PL.g4](./src/main/antlr/PL.g4)
- [PLLexerPart.g4](./src/main/antlr/PLLexerPart.g4)

## Runtime Specification

The signature (in SAMPL) and implementation (in Kotlin) of the provided runtime functions are given 
in the Kotlin file 
[PrimitiveRuntimeLibrary](./src/main/kotlin/org/sampl/runtime/PrimitiveRuntimeLibrary.kt).

Note that the primitive types are different in Kotlin and SAMPL. The correspondence is listed in
the table below.

| SAMPL    | Kotlin    |
| :------: | :-------: |
| `Unit`   | `Unit`    |
| `Int`    | `Long`    |
| `Float`  | `Double`  |
| `Bool`   | `Boolean` |
| `Char`   | `Char`    |
| `String` | `String`* |

Note: `String` is not a primitive type in Kotlin.

## Namespace Specification

### Scope and Mutually Recursive Problem

Lexical scope is used for type checking, so cyclic dependencies between different classes is 
strictly prohibited. Within each class, type declaration can be recursive. Currently, even this is 
allowed: `class BadExample(value: BadExample)` Classes are all public and static. Types are public
but its definitions can only be used within the scope of the class.

Constants of a class are static. They must appear first in class members declarations and cannot be
mutually recursive with any other member in the entire program. All functions within the class are 
treated as they are all mutually recursive.

As stated before, nested classes cannot be mutually recursive.

### Member Reference

To refer to a member in the current class, you simply use the name. If you are not in the class, 
then you need to use qualified name. For example:

```
class Foo {

  class Bar {
    let a = 1
  }
  
  class HelloWorld {
    let b = a // WRONG :(
    let c = Bar.a // Correct :)
  }

}
```

## Type Checking Specification

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
