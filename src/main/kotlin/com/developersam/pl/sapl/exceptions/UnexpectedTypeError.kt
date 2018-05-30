package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.TypeInformation

/**
 * [UnexpectedTypeError] reports an unexpected type during compile time type checking.
 *
 * @param expectedType the expected type according to the context.
 * @param actualType actual type deduced from the expression.
 */
internal class UnexpectedTypeError(
        private val expectedType: String,
        private val actualType: TypeExprInAnnotation
) : CompileTimeError(reason = "Unexpected type: $actualType. Expecting: $expectedType.") {

    /**
     * Construct the same error but with [expectedType] as an [TypeExprInAnnotation].
     */
    constructor(expectedType: TypeExprInAnnotation, actualType: TypeExprInAnnotation) : this(
            expectedType = expectedType.toString(), actualType = actualType
    )

}
