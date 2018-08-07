# SAMPL - Sound And Modern Programming Language

[![Build Status](https://travis-ci.com/SamChou19815/sampl.svg?branch=master)](https://travis-ci.com/SamChou19815/sampl)
[![Release](https://jitpack.io/v/SamChou19815/sampl.svg)](https://jitpack.io/#SamChou19815/sampl)
![GitHub](https://img.shields.io/github/license/SamChou19815/sampl.svg)

<img src="https://developersam.com/assets/app-icons/sampl.png" alt="SAMPL" width="300" height="300"/>

It is a JVM language that embraces the functional programming paradigm, which currently supports 
immutable data structures, null safety, pattern matching, currying, limited type inference, and 
limited interop with other JVM languages.

Read the docs [here](http://docs.developersam.com/sampl/).

## Gradle Config

Add this to your `build.gradle` to use the artifact.

```groovy
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}
dependencies {
    implementation 'com.github.SamChou19815:sampl:+'
}
```

## Scope of this Project

This project aims to implement a type-checker, interpreter, and compiler for this language. 

The type-checker will be invoked before both interpretation and compilation to reject all ill-formed
code before running. We aim to design a sound type system for this language without any 
qualification, so this language does not support type cast that can potentially produce type errors
at runtime.

The interpreter will not be an REPL. Instead, it only supports the interpretation of an entire 
program. The compiler will compile the source code of this language to Kotlin code, then invoke
the Kotlin compiler to compile it to Java bytecode. This design allows rapid prototyping and lets
the generated bytecode have excellent integration with Kotlin codebase.

## Notable Features

### Type Inference

```kotlin
val a = 42
val b = a + 4
fun main(): Int = a + b
```

### Pattern Matching

```
class Optional<T>(None | Some of T) {
   fun <T> hasValue(v: Optional<T>): Bool = 
     match v with
     | None -> false
     | Some _ -> true
}
```

### Currying

```kotlin
fun add(a: Int, b: Int): Int = a + b
val add1 = add(1)
fun main(): Int = add1(2)
```

### Purely Functional

Since SAMPL is a pure functional language, it can be used to illustrate the concept of 
"Propositions are types and proofs are programs".

#### Propositions are SAMPL types

A type parameter in generics info (e.g. `A` in `Or<A, B>`) is an atomic proposition.

The `Unit` type represents `TRUE`.

```kotlin
// The [And] class below represents logical conjunction.
class And<A, B>(a: A, b: B)
```

```
// The [Or] class below represents logical disjunction.
class Or<A, B>(
  First of A | Second of B
)
```

```kotlin
// This function has type (A) -> B. This is logical implication.
fun <A, B> implication(a: A): B = b
```

#### Proofs are SAMPL programs

```kotlin
// This well-typed function is an elegant proof for Modus Ponens.
fun <A, B> modusPonens(f: (A) -> B, v: A): B = f(v)
```

```kotlin
// This well-typed function is an elegant proof for both A and B are true.
fun <A, B> both(a: A, b: B): And<A, B> = And(a, b)
```

```
// This well-typed function is an elegant proof for either A or B implies C.
fun <A, B, C> eitherOneImplies(o: Or<A, B>): C = 
  match o with
  | First a -> aToC(a)
  | Second b -> bToC(b)
```

```
// This well-typed function shows everything proves true.
fun <A> everythingProvesTrue(a: A): Unit = () 
```

## Getting Started

Pass `-interpret [filename]` to the jar to interpret the program in `filename`. Currently, it will
prints the result to standard out.

Pass `-compile [filename]` to the jar to compile the program in `filename`. Currently, it will 
prints the equivalent code in Kotlin to standard out.

## Documentations

- Read the [language spec](./LANGUAGE_SPEC.md) for the definition of the language features. 
*Currently, the language is not very precise.*

- Read the [design document](./DESIGN_DOCS.md) to understand the overall design and architecture.

## Developer Notes

To reduce the size of the package, we do not add the Kotlin compiler as a dependency. If you need
a self-contained package, you can easily wrap it. The reason is that people may only need the 
interpreter features, so the 30M Kotlin compiler is completely useless to them.

This project is still in prototype. There will be no backward-compatible guarantees in the near
feature. Especially do not use this in production.

## Current Status

- Version: Alpha 2
- License: MIT
- Contributors: `["Sam Zhou"]`
