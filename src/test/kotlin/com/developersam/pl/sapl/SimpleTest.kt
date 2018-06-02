package com.developersam.pl.sapl

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
        /* Unit is true */

        type And<A, B> = {
            a: A; b: B
        }

        type Or<A, B> = | First of A | Second of B

        let hey = "A -> B is implication"

        let modusPonens (f: A -> B) (v: A): B = f(v)

    """.trimIndent()

    /**
     * [run] simply runs some code to show that the system is working.
     */
    @Test
    fun run() {
        PLCompiler.compileFromSource(code = propositionsAreTypesProofsAreProgram)
    }

}
