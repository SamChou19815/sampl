package com.developersam.pl.sapl.exceptions

/**
 * [GenericInfoWrongNumberOfArgumentsError] reports the problem of generics information's wrong
 * number of arguments to the compiler.
 */
internal class GenericInfoWrongNumberOfArgumentsError(
        val expectedNumber: Int = 0, val actualNumber: Int
) : CompileTimeError(reason = "Wrong number of arguments for generic information. " +
        "Expected: $expectedNumber, Actual: $actualNumber.")
