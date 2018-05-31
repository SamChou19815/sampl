package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.ast.TypeExprInAnnotation

/**
 * [toFunctionTypeExpr] converts the function type annotation in list form with [argumentTypes]
 * and [returnType] to a single BST form.
 */
internal fun toFunctionTypeExpr(argumentTypes: List<TypeExprInAnnotation>,
                                returnType: TypeExprInAnnotation): TypeExprInAnnotation.Function {
    var t = TypeExprInAnnotation.Function(
            argumentType = argumentTypes[argumentTypes.size - 1], returnType = returnType
    )
    for (i in argumentTypes.size - 2 downTo 0) {
        t = TypeExprInAnnotation.Function(argumentType = argumentTypes[i], returnType = t)
    }
    return t
}
