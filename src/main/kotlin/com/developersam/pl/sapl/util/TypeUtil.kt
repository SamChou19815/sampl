package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.ast.FunctionTypeInAnnotation
import com.developersam.pl.sapl.ast.TypeExprInAnnotation

/**
 * [toFunctionTypeExpr] converts the function type annotation in list form with [argumentTypes]
 * and [returnType] to a single BST form.
 */
internal fun toFunctionTypeExpr(argumentTypes: List<TypeExprInAnnotation>,
                                returnType: TypeExprInAnnotation): TypeExprInAnnotation {
    var t = returnType
    for (i in argumentTypes.size - 1 downTo 0) {
        t = FunctionTypeInAnnotation(argumentType = argumentTypes[i], returnType = t)
    }
    return t
}
