package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.TypeExpr
import java.util.Arrays

/**
 * [GenericTypeInfoDoesNotMatchError] reports the problem of disagreement in generic declaration
 * site and use site.
 *
 * @param genericDeclarations the generic declaration during type declaration.
 * @param genericTypeExpr the type expr parameterized by [genericDeclarations].
 * @param actualTypeExpr the actual type expression in the use site.
 * @param knownGenericInfo optional known info.
 */
class GenericTypeInfoDoesNotMatchError(
        val genericDeclarations: List<String>,
        val genericTypeExpr: TypeExpr, val actualTypeExpr: TypeExpr,
        val knownGenericInfo: Array<TypeExpr?>
) : CompileTimeError(
        reason = """
    Generic information does not match in declaring site and use site.
    Declared Site: <$genericDeclarations> $genericTypeExpr.
    Use Site: $actualTypeExpr.
    Already Known/Inferred Info: ${Arrays.toString(knownGenericInfo)}.
    """.trimIndent()
) {

    /**
     * [InternalError] is used internally to indicate a [GenericTypeInfoDoesNotMatchError] but
     * with incomplete information at the use site.
     */
    internal class InternalError : Exception()

}
