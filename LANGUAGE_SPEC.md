# SAMPL Language Specification

## Grammar Specification

You can read the spec for grammar by reading the code at

- [PL.g4](./src/main/antlr/PL.g4)
- [PLLexerPart.g4](./src/main/antlr/PLLexerPart.g4)

## Runtime Specification

The signature and implementation of the provided runtime functions are given in the Kotlin file 
[PrimitiveRuntimeLibrary](./src/main/kotlin/org/sampl/runtime/PrimitiveRuntimeLibrary.kt).

SAMPL has a set of predefined types, which (somewhat) corresponds to the primitive types in Kotlin
and JVM. The correspondence is listed in the table below.

Note that the primitive types are different in Kotlin and SAMPL. 

| SAMPL         | Kotlin           |
| :-----------: | :--------------: |
| `Unit`        | `Unit`           |
| `Int`         | `Long`           |
| `Float`       | `Double`         |
| `Bool`        | `Boolean`        |
| `Char`        | `Char`           |
| `String`      | `String`*        |
| `StringArray` | `Array<String>`* |

Note: `String` and `Array<String>` are not primitive types in Kotlin and JVM.

## Namespace Specification

### Scope and Mutually Recursive Problem

Lexical scope is used for type checking, so cyclic dependencies between different classes is 
strictly prohibited. Within each class, type declaration can be recursive. Currently, even this is 
allowed: `class BadExample(value: BadExample)` Classes are all public and static. Types are public
but its definitions can only be used within the scope of the class.

Inside a class contains class members. A program also contains those members.

Members can contain constants, functions and nested class in any order. Cyclic dependencies are
not allowed, except that all functions inside a function groups are mutually recursive. A function
group is the maximum continuous sequence of function declarations in class.

### Member Reference

To refer to a member in the current class, you simply use the name. If you are not in the class, 
then you need to use qualified name. For example:

```kotlin
class Foo {
  val a = 1
}
  
class Bar {
  val b = a // WRONG :(
  val c = Foo.a // Correct :)
}
```

### Name Shadowing

This is bad and is not allowed.

## Code Structure Specification

The program is a simple file that contains a single class. Each class starts with type definition
in a constructor like parenthesis, and then defines constants, functions, and other nested classes
in order. 

Constants, functions, and nested classes are called *members* of a class. The code that specifies
those members are called *definitions*. For constant and function definitions, their bodies are
code that computes something, which is called *expression*.

Expressions and definitions have slightly different type checking and evaluation rules.

## Type Checking Specification

### Type Inference and Its Current Limitation

Currently, all parameters in a function must be annotated by their types. For functions as a class 
member, their return types must be annotated; for functions as a value, their return types must not
be annotated. All variables do not have type annotation. The type inference algorithm will 
automatically figure that out. 

(In later versions, we may add support for optional type annotation and respect those annotations.)

Also, we only support generic functions at class member level. Functions are not generic in any 
expression.

### Type Checking Rules

#### Definitions

##### Class Constant Member

Syntax: `val identifier = expr`

If `expr` has type `t`, then the environment `e` will gain an additional mapping from `identifier`
to `t`.

##### Class Function Member

Syntax: `fun (<A, B, ...>)? identifier(arg1: T1, arg2: T2, ...): RT = expr`.

The function has type `(T1, T2, ...) -> RT`. The `<expr>` must also have type `expr`. During type
checking, generics info `A, B, ...` will be added to the known types and `args1` to `T1`, `args2` to
`T2`, etc will be added to the environment when type checking `expr`. In addition, this function and
all other functions type will be stored in the environment during type checking.

#### Expressions

##### Literal

e.g. `()`, `1`, `2.0`, `true`, `'e'` `"Hello World"`

Type is simply the type of the literal, which can be one of `Unit`, `Int`, `Float`, `Bool`, `Char`
or `String`.

##### Variable

Type is the type of the variable in the environment. If generics info is available, the variable
will have the specific type specified by the generics information.

##### Constructor

Type is the type of the class that defines the constructor.

##### Struct Member Access

Syntax: `a.b`.

Type is the type of the member `b` of struct `a`. `a` must be a struct and has member `b`.

##### Not Expression

Syntax: `!expr`.

`expr` must have type `Bool` and this expression has type `Bool`.

##### Binary Expression

Syntax: `a op b`.

`a` and `b` must have the same type and `op` is a legal binary operator.

- If `op` is `shl`, `shr`, `ushr`, `xor`, `land`, `lor`, `*`, `/`, `%`, `+`, or `-`, `a` and `b`
must both have type `Int`, and the expression has type `Int`.
- If `op` is `*.`, `/.`, `+.`, or `-.`, `a` and `b` must both have type `Float`, and the expression
has type `Float`.
- If `op` is `^`, `a` and `b` must both have type `String`, and the expression has type `String`.
- If `op` is `<`, `<=`, `>`, or `>=`, `a` and `b` must both be a predefined type (but not
`StringArray`), and the expression has type `Bool`.
- If `op` is `==` or `!=`, `a` and `b` must have the same type and the expression has type `Bool`.
- If `op` is `&&` or `||`, `a` and `b` must have type `Bool` and the expression has type `Bool`.

##### Throw Expression

Syntax: `throw<T> expr`.

`expr` must have type `String` and the expression has type `T`.

##### If Expression

Syntax: `if c then e1 else e2`.

`c` must have type `Bool`. `e1` and `e2` must have the same type, and the expression's type is the
type of `e1`/`e2`.

##### Match Expression

Syntax: 
```
match expr with 
| pattern1 -> e1 
| pattern2 -> e2 
| ...
```

`expr` must be a variant and all the patterns must be related to the type of variant. `e1`, `e2`,
etc must have the same type and the expression has the same type as `e1`/`e2`/...

##### Function Application

Syntax: `funExpr (arg1, arg2, ...)`.

`funExpr` must be a function expression, whose number of available arguments must be greater than or
equal to the given arguments. The type of the expression is determined according to function
currying rule.

##### Function

Syntax: `{ (arg1: T1, arg2: T2, ...) -> expr }`.

If the type of `expr` is `RT`, then this  expression has type `(T1, T2, ...) -> RT`. During type
checking for `expr`, `args1` to `T1`, `args2` to `T2`, etc will be added to the environment.

##### Try Catch Expression

Syntax: `try tryExpr catch e catchExpr`.

`tryExpr` and `catchExpr` must have the same type and this expression has the same type as
`tryExpr`/`catchExpr`. When type checking `catchExpr`, `e` to `String` will be added to the
environment.

##### Let Expression

Syntax: `val a = e1; e2`.

If `e1` has type `T` then `a` to `T` will be added to the environment when type checking `e2`. This
expression has the same type as `e2`.

## Evaluation Specification

The program will evaluate to a value according to these two rules:

1. If there is a no-arg main function in the top-level class, the value of the function is the 
return value of that function
2. Otherwise, the value is Unit.

### Types of Values

- IntValue
- FloatValue
- BoolValue
- CharValue
- StringValue
- StringArrayValue
- VariantValue
- StructValue
- ClosureValue

### Evaluation Rules

#### Definitions

##### Class Constant Member

Syntax: `val identifier = expr`

If `expr` has value `v`, then the environment `e` will gain an additional mapping from `identifier`
to `v`.

##### Class Function Member

Syntax: `fun (<A, B, ...>)? identifier(arg1: T1, arg2: T2, ...): RT = expr`

The environment will be added a closure with this function and a environment that contains all the
functions defined in the class that this function is in.

#### Expressions

##### Literal

e.g. `()`, `1`, `2.0`, `true`, `'e'` `"Hello World"`

Value is simply the value of the literal.

##### Variable

Value is the value of the variable in the environment.

##### Constructor

Value is the value of the struct or variant with associated data defined in class.

##### Struct Member Access

Syntax: `a.b`.

Value is the value of the member `b` of struct `a`. `a` must be a struct and has member `b`.

##### Not Expression

Syntax: `!expr`.

If `expr` evaluates to `b`, then this expression has value as the inversion of `b`.

##### Binary Expression

Syntax: `a op b`.

- If `op` is `shl`, `shr`, `ushr`, `xor`, `&`, `|`, `*`, `/`, `%`, `+`, `-`, `<`, `<=`, `>`, `>=`,
`==`,`!=`, `&&` or `||`, `a op b`'s behavior is same in SAMPL and Kotlin.
- If `op` is `*.`, `/.`, `+.`, or `-.`, without the dot, `a op b`'s behavior is same in SAMPL and
Kotlin.
- If `op` is `^`, `a ^ b` is the string concat of `a` and `b`.

##### Throw Expression

Syntax: `throw<T> expr`.

If `expr` evaluates to `v`, an exception with message `v` will be thrown.

##### If Expression

Syntax: `if c then e1 else e2`.

If `c` evaluates to `true` and `e1` evaluates to `v1`, then the expression has value `v1`. If `c`
evaluates to `false` and `e2` evaluates to `v2`, then the expression has value `v2`.

##### Match Expression

Syntax: 
```
match expr with 
| pattern1 -> e1 
| pattern2 -> e2 
| ...
```

It will goes to the pattern that fits `expr`, then puts the identifiers and associated data into the
environment if there exists associated data. Then the expression associated to the pattern's value
will be the value of this expression.

##### Function Application

Syntax: `funExpr (arg1, arg2, ...)`.

The value of the expression is determined according to function currying rule.

##### Function

Syntax: `{ (arg1: T1, arg2: T2, ...) -> expr }`.

It will be evaluate to a closure.

##### Try Catch Expression

Syntax: `try tryExpr catch e catchExpr`.

If `tryExpr` evaluates to `v1` without throwing an exception, then the value of the expression is
`v1`. Else, the binding `e` to the exception message will be added to the environment, and 
`catchExpr` will be evaluated to get `v2` in the new environment and the value of the expression is
`v2`.

##### Let Expression

Syntax: `val a = e1; e2`.

If `e1` evaluates to `v1` then `a` to `b2` will be added to the environment when evaluating `e2`.
This expression has the value of the value of `e2`.
