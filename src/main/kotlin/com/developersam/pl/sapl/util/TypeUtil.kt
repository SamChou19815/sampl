package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.ast.TypeExpr

/**
 * [toFunctionTypeExpr] converts the function type annotation in list form with [argumentTypes]
 * and [returnType] to a single BST form.
 */
internal fun toFunctionTypeExpr(argumentTypes: List<TypeExpr>,
                                returnType: TypeExpr): TypeExpr.Function {
    var t = TypeExpr.Function(
            argumentType = argumentTypes[argumentTypes.size - 1], returnType = returnType
    )
    for (i in argumentTypes.size - 2 downTo 0) {
        t = TypeExpr.Function(argumentType = argumentTypes[i], returnType = t)
    }
    return t
}
