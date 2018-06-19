package org.sampl.exceptions

import org.sampl.ast.type.TypeExpr

/**
 * [UnexpectedTypeError] reports an unexpected type during compile time type checking.
 *
 * @param expectedType the expected type according to the context.
 * @param actualType actual type deduced from the expression.
 */
class UnexpectedTypeError(
        lineNo: Int, expectedType: String, actualType: TypeExpr
) : CompileTimeError(reason = "Line $lineNo: Unexpected type: $actualType; " +
        "Expected: $expectedType.") {

    companion object {

        /**
         * [check] checks whether [actualType] matches [expectedType] at [lineNo]. If not, it will
         * throw [UnexpectedTypeError].
         */
        @JvmStatic
        fun check(lineNo: Int, expectedType: TypeExpr, actualType: TypeExpr) {
            if (expectedType != actualType) {
                throw UnexpectedTypeError(lineNo, expectedType.toString(), actualType)
            }
        }

    }

}
