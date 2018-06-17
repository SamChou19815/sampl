# Design Document

This design document aims to provide the workflow of the interpretation and compilation, a 
high-level overview of the structures of the codebase, and some conventions in the implementation.
In the end, we also admit some known problems and we will use them as the roadmap before we reach
stable.

## Workflow: 

This section explains the multi-stage interpretation and compilation.

The interpreter and the compiler both shares these stages:

1. Lexing/Tokenizing: It converts the string format of the code to a stream of tokens. The tokens
are defined in [PLLexerPart.g4](./src/main/antlr/PLLexerPart.g4), and this step is taken care by 
ANTLR.
2. Parsing: It first converts the stream of tokens to a parse tree, which is done by ANTLR. Then our
program will convert the parse tree to AST (declared in `ast` package), which is mostly done by the 
`parser` package.
3. Runtime-Injection: It will inject some runtime functions into the program definitions to allow
some useful operations (e.g. I/O). The primitive and provided runtime can both be injected. 
Primitive runtime library contains basic functions for the language that is impossible to implement
in this language (e.g. I/O). This runtime will always be injected. The user can also choose to
inject a provided runtime which is responsible for the interaction between this language and another
JVM language. This step is mostly done in the `runtime` package.
4. Type-Checking: It will check whether the program is well-formed according to the 
[language spec](./LANGUAGE_SPEC.md). It is mostly done inside the `ast` packages's node classes.
In particular, it transforms the raw AST into type-decorated AST, where each expression is decorated
with a type. The raw AST is in the `ast.raw` package and the decorated ones are in `ast.decorated`
package. Some common elements are declared in `ast.common`, and the common type expression are in
package `ast.type`. 

After the common stages described above, the interpreter will directly interpret the decorated AST.

~~Before the common stages, the compiler will first perform an dependency analysis. The interpreter
only supports a single file as the source code, while the compiler supports multiple files in the
same directory. However, only single-file mode is supported in the later stages, and under the hood
the multi-file support is only a structural sugar for the single-file support. The compiler will 
build a dependency graph from the imports declarations, and use them to resolve dependencies. The
compiler will reject cyclic dependencies because they are bad. The output of this stage is a list of
files that represent the compilation sequence, which is then converted to nested classes in a single
class.~~ (Not supported yet.)

After the common stages, the compiler will directly translate the decorated AST to Kotlin code. 
Since there is a close correspondence between the code in this language and Kotlin code (thanks to
FP support in Kotlin), we do not need various IR lowering. This is done mostly in the packages
`ast.decorated` and `codegen`. Finally, we will write the translated code to file and invoke the 
Kotlin compiler. This step is trivial. (This is done in the test, not the main code.)

## High-Level Overview

Since the lexing and parsing part is mostly done by ANTLR, and the dependency analysis is merely a
variation of topological sort, we will start the overview at the runtime-injection stage.

### Runtime Injection

Runtime injection is implemented by reflection. The functions needed to be injected will be
marked by the `@RuntimeFunction` annotation. These functions must be static and contains only
primitive or simple generics parameters. One exception is string array. The library class must
implement the `RuntimeLibrary` marker interface.

In Kotlin, it can be done like this:

```kotlin
class ProvidedLibrary : RuntimeLibrary {

    @RuntimeFunction
    @JvmStatic
    fun test(a: Long): Double = a.toDouble()
}
```

Currently in the interpreter both primitive and provided library functions are called by reflection.
More efficient implementation will be used in the future.

### Type Checking

The type checking uses 
[the environment model](http://www.cs.cornell.edu/courses/cs3110/2018sp/l/18-env-model/lec.pdf) with
immutable data structures provided by [Okaml-Lib](https://github.com/SamChou19815/Okaml-Lib).

The type checking methods in the data class in the package `ast.raw` take the given environment to 
deduce types of itself and produce a type decorated data class (usually prefixed with `Decorated`) 
in package `ast.decorated`. These methods may use mutable features for its implementation, but they
have no *visible* side effects.

### Interpretation

Interpretation is also implemented by the environment model. Interpretation itself does not need
the type information, so type is not recorded in the environment. 

Currently, the mutually recursive functions environment is dynamically patched.

### Compilation

The compilation and the pretty printing shares the same visitor interface and exhibit similar 
structures. The AST nodes are not responsible for these complex logic; instead, they simply accept
the code generation visitor, which we call `AstToCodeConverter`. This flexible structure allows us
to support even more target code (although this is not a priority). 

## Conventions

### Code Style

Please refer to the 
[Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html). 
Also, we plan to enforce 100-character limit.

### Paradigm

This project uses a mix of object-oriented programming and functional programming. No preference is
given to either one, and they will be used when fit. For example, visitor pattern, pattern matching,
and usage of dynamic dispatch all appear in the code base. 

However, we do prefer immutable data structures over mutable ones.

### Naming Conventions

*Note: This is not a discussion about camelCase vs. snake_case. We use camelCase.*

- The type checker functions are named `typeCheck`.
- Indentation is usually written as `idt`.

## Known Problems

- The error messages are very bad. In the AST construction process, line info is not added to the 
AST for the convenience of rapid prototyping. It will be improved later.
- Type checking, interpretation, and code generation has not been thoroughly tested. They are 
expected to have at least 20 bugs or some undefined behavior.
- Function Reference is different in SAMPL and Kotlin. Currently code generation with function 
reference has some problem (e.g. it cannot correctly convert from `funName` to `::funName`).
