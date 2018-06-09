# Design Document

This design document aims to provide the workflow of the interpretation and compilation, a 
high-level overview of the structures of the codebase, and some conventions in the implementation.

## Workflow: 

This section explains the multi-stage interpretation and compilation.

### Common Stages

The interpreter and the compiler both shares these stages:

1. Lexing/Tokenizing: It converts the string format of the code to a stream of tokens. The tokens
are defined in [PLLexerPart.g4](./src/main/antlr/PLLexerPart.g4), and this step is taken care by 
ANTLR.
2. Parsing: It first converts the stream of tokens to a parse tree, which is done by ANTLR. Then our
program will convert the parse tree to AST (declared in `ast` package), which is mostly done by the 
`parser` package.
3. Runtime-Injection: It will inject some runtime functions into the program definitions to allow
some useful operations (e.g. I/O). *Currently, this part is not implemented yet.*
4. Type-Checking: It will check whether the program is well-formed according to the 
[language spec](./LANGUAGE_SPEC.md). It is mostly done inside the `ast` packages's node classes.
In particular, it transforms the raw AST into type-decorated AST, where each expression is decorated
with a type. The raw AST is in the `ast.raw` package and the decorated ones are in `ast.decorated`
package. Some common elements are declared in `ast.common`, and the common type expression are in
package `ast.type`. 

### Interpreter Specific Stages

After the common stages described above, the interpreter will directly interpret the decorated AST.

*Currently, this part is not implemented yet.*

### Compiler Specific Stages

Before the common stages, the compiler will first perform an dependency analysis. The interpreter
only supports a single file as the source code, while the compiler supports multiple files in the
same directory. However, only single-file mode is supported in the later stages, and under the hood
the multi-file support is only a structural sugar for the single-file support. The compiler will 
build a dependency graph from the imports declarations, and use them to resolve dependencies. The
compiler will reject cyclic dependencies because they are bad. The output of this stage is a list of
files that represent the compilation sequence, which is then converted to nested classes in a single
class.

After the common stages, the compiler will directly translate the decorated AST to Kotlin code. 
Since there is a close correspondence between the code in this language and Kotlin code (thanks to
FP support in Kotlin), we do not need various IR lowering. This is done mostly in the packages
`ast.decorated` and `codegen`. 

Finally, we will write the translated code to file and invoke the Kotlin compiler. This step is
trivial.

## High-Level Overview

Since the lexing and parsing part is mostly done by ANTLR, and the dependency analysis is merely a
variation of topological sort, we will start the overview at the runtime-injection stage.

### Runtime Injection

*Currently, this part is not implemented yet.*

`TODO`

### Type Checking

The type checking uses 
[the environment model](http://www.cs.cornell.edu/courses/cs3110/2018sp/l/18-env-model/lec.pdf) with
immutable data structures provided by [Okaml-Lib](https://github.com/SamChou19815/Okaml-Lib).

The type checking methods in the data class in the package `ast.raw` take the given environment to 
deduce types of itself and produce a type decorated data class (usually prefixed with `Decorated`) 
in package `ast.decorated`. These methods may use mutable features for its implementation, but they
have no *visible* side effects.

### Interpretation

*Currently, this part is not implemented yet.*

`TODO`

### Compilation

`TODO there is a great structral change going on...`

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

*Note: This is not a discussion about CamelCase vs. snake_case.*

- The type checker functions are named `typeCheck`.
- The functions that convert AST to well-indented source code are named `toIndentedSourceCode`.
- The functions that convert AST to well-indented Kotlin code are named `toIndentedCompiledCode`
- The functions that convert AST to one-liner source code are named `toOneLineSourceCode`.
- The functions that convert AST to one-liner Kotlin code are named `toOneLineCompiledCode`
