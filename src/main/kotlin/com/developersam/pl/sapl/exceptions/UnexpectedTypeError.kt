package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.type.TypeExpr

/**
 * [UnexpectedTypeError] reports an unexpected type during compile time type checking.
 *
 * @param expectedType the expected type according to the context.
 * @param actualType actual type deduced from the expression.
 */
class UnexpectedTypeError(
        private val expectedType: String, private val actualType: TypeExpr
) : CompileTimeError(reason = "Unexpected type: $actualType. Expecting: $expectedType.") {

    /**
     * Construct the same error but with [expectedType] as an [TypeExpr].
     */
    constructor(expectedType: TypeExpr, actualType: TypeExpr) : this(
            expectedType = expectedType.toString(), actualType = actualType
    )

}
