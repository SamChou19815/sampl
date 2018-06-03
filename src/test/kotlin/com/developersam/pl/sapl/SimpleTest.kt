package com.developersam.pl.sapl

import com.developersam.pl.sapl.modules.ModuleConstructor
import org.junit.Test

/**
 * [SimpleTest] contains some simple programs to demonstrate the working status of the system.
 */
class SimpleTest {

    /**
     * [propositionsAreTypesProofsAreProgram] is a program that illustrates the concept of
     * 'Propositions Are Types, Proofs Are Programs'.
     */
    private val propositionsAreTypesProofsAreProgram: String = """
        type And<A, B> = {
            a: A; b: B
        }
        type Or<A, B> =
            | First of A
            | Second of B
        let trueVar = () /* Unit is true */
        let implication = function (a: String) -> 5 // (String -> Int) Implication
        let <A, B> modusPonens (f: A -> B) (v: A): B = f(v)
    """.trimIndent()

    /**
     * [run] simply runs some code to show that the system is working.
     */
    @Test
    fun run() {
        println(propositionsAreTypesProofsAreProgram)
        println()
        val firstCompile = ModuleConstructor
                .fromSource(code = propositionsAreTypesProofsAreProgram)
                .typeCheck()
                .members
        println(firstCompile.prettyPrint())
        val secondCompile = firstCompile.prettyPrint()
                .let { ModuleConstructor.fromSource(code = it).typeCheck().members }
        println(secondCompile.prettyPrint())
    }

}
