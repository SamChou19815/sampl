package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.type.TypeExpr
import java.util.Arrays

/**
 * [GenericsError] is a collection of errors related to generics.
 */
sealed class GenericsError(reason: String) : CompileTimeError(reason = reason) {

    /**
     * [GenericsInfoWrongNumberOfArguments] reports the problem of generics information's wrong
     * number of arguments to the compiler.
     */
    class GenericsInfoWrongNumberOfArguments(val expectedNumber: Int = 0, val actualNumber: Int)
        : CompileTimeError(reason = "Wrong number of arguments for generic information. " +
            "Expected: $expectedNumber, Actual: $actualNumber.")


    /**
     * [GenericsTypeInfoDoesNotMatch] reports the problem of disagreement in generic declaration
     * site and use site.
     *
     * @param genericDeclarations the generic declaration during type declaration.
     * @param genericTypeExpr the type expr parameterized by [genericDeclarations].
     * @param actualTypeExpr the actual type expression in the use site.
     * @param knownGenericInfo optional known info.
     */
    class GenericsTypeInfoDoesNotMatch(
            val genericDeclarations: List<String>,
            val genericTypeExpr: TypeExpr? = null, val actualTypeExpr: TypeExpr? = null,
            val knownGenericInfo: Array<TypeExpr?>
    ) : GenericsError(
            reason = """
    Generic information does not match in declaring site and use site.
    Declared Site: <$genericDeclarations> ${genericTypeExpr?.toString() ?: "[Unknown]"}.
    Use Site: ${actualTypeExpr?.toString() ?: "[Unknown]"}.
    Already Known/Inferred Info: ${Arrays.toString(knownGenericInfo)}.
    """.trimIndent()
    ) {

        /**
         * [InternalError] is used internally to indicate a [GenericTypeInfoDoesNotMatch] but
         * with incomplete information at the use site.
         */
        internal class InternalError : Exception()

    }

}
