@file:JvmName(name = "TypeUtil")

package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.ast.TypeExpr
import com.developersam.pl.sapl.exceptions.GenericTypeInfoDoesNotMatchError

/**
 * [toFunctionTypeExpr] converts the function type annotation in list form with [argumentTypes]
 * and [returnType] to a single BST form.
 */
internal fun toFunctionTypeExpr(
        argumentTypes: List<TypeExpr>, returnType: TypeExpr
): TypeExpr.Function {
    var t = TypeExpr.Function(
            argumentType = argumentTypes[argumentTypes.size - 1], returnType = returnType
    )
    for (i in argumentTypes.size - 2 downTo 0) {
        t = TypeExpr.Function(argumentType = argumentTypes[i], returnType = t)
    }
    return t
}

/**
 * [PairedTypeExprVisitor] visits the pair [genericType] and [actualType] in sync and
 * tries to reconcile for [oneGenericDeclaration], under [knownGenericInfo].
 */
private data class PairedTypeExprVisitor(
        private val oneGenericDeclaration: String, private val knownGenericInfo: Array<TypeExpr?>,
        private val genericType: TypeExpr, private val actualType: TypeExpr
) {

    /**
     * [die] declares failure.
     */
    @Throws(GenericTypeInfoDoesNotMatchError.InternalError::class)
    private fun die(): Nothing = throw GenericTypeInfoDoesNotMatchError.InternalError()

    /**
     * [visit] visits the pairs and try to reconcile between declared and actual.
     *
     * @return the type expression of [genericTypeExpr] with known generic info replaced with
     * actual ones.
     */
    @Throws(GenericTypeInfoDoesNotMatchError.InternalError::class)
    fun visit(): TypeExpr {
        return if (genericType is TypeExpr.Identifier && actualType is TypeExpr.Identifier) {
            TODO()
        } else if (genericType is TypeExpr.Function && actualType is TypeExpr.Function) {
            val partiallyKnownArgumentType = copy(
                    genericType = genericType.argumentType,
                    actualType = genericType.argumentType
            ).visit()
            val partiallyKnownReturnType = copy(
                    genericType = genericType.returnType,
                    actualType = actualType.returnType
            ).visit()
            TypeExpr.Function(
                    argumentType = partiallyKnownArgumentType,
                    returnType = partiallyKnownReturnType
            )
        } else if (genericType is TypeExpr.Identifier && actualType is TypeExpr.Function) {
            TODO()
        } else {
            die()
        }
    }

}

/**
 * [inferActualGenericTypeInfo] tries to infer the generic info from [genericDeclarations],
 * [genericTypeExpr] and [actualTypeExpr] and tries to reconcile between declaring site and use
 * site
 * It stores the known/inferred info inside [knownGenericInfo], where a `null` value in the array
 * means non-determined actual type expression.
 * If this operation fails, it will throw [GenericTypeInfoDoesNotMatchError].
 */
internal fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>, genericTypeExpr: TypeExpr, actualTypeExpr: TypeExpr,
        knownGenericInfo: Array<TypeExpr?>
) {

}

/**
 * [inferActualGenericTypeInfo] tries to infer the generic info from [genericDeclarations],
 * [genericTypeExpr] and [actualTypeExpr] and tries to reconcile between declaring site and use
 * site.
 * If this operation fails, it will throw [GenericTypeInfoDoesNotMatchError].
 */
internal fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>, genericTypeExpr: TypeExpr, actualTypeExpr: TypeExpr
): List<TypeExpr> {
    val knownInfo = arrayOfNulls<TypeExpr>(size = genericDeclarations.size)
    inferActualGenericTypeInfo(genericDeclarations, genericTypeExpr, actualTypeExpr, knownInfo)
    val knownInfoAsList = knownInfo.filterNotNull()
    if (knownInfoAsList.size != knownInfo.size) {
        throw GenericTypeInfoDoesNotMatchError(
                genericDeclarations, genericTypeExpr, actualTypeExpr, knownInfo
        )
    }
    return knownInfoAsList
}
