package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.TypeIdentifier

/**
 * [UnexpectedTypeError] reports an unexpected type during compile time type checking.
 *
 * @param expectedType the expected type according to the context.
 * @param actualType actual type deduced from the expression.
 */
internal class UnexpectedTypeError(
        private val expectedType: TypeIdentifier,
        private val actualType: TypeIdentifier
) : CompileTimeError(reason = "Unexpected type: $actualType. Expecting: $expectedType.")
