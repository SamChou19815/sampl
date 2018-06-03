@file:JvmName(name = "TypeUtil")

package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.ast.type.TypeExpr
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
 * [joinToGenericsInfoString] converts a list of info to generics info and returns the string.
 */
internal fun <T> List<T>.joinToGenericsInfoString(): String =
        joinToString(separator = ",", prefix = "<", postfix = ">")

/**
 * [PairedTypeExprVisitor] visits the pair [genericType] and [actualType] in sync and
 * tries to reconcile for [oneGenericDeclaration] at [index], under [knownGenericInfo].
 */
@Suppress(names = ["ArrayInDataClass"])
private data class PairedTypeExprVisitor(
        private val oneGenericDeclaration: String, private val index: Int,
        private val knownGenericInfo: Array<TypeExpr?>,
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
     * @return the type expression of [genericType] with known generic info replaced with
     * actual ones.
     */
    @Throws(GenericTypeInfoDoesNotMatchError.InternalError::class)
    fun visit(): TypeExpr {
        return if (genericType is TypeExpr.Identifier && actualType is TypeExpr.Identifier) {
            if (genericType.type == oneGenericDeclaration) {
                // Name matches the generic name, expect to replace!
                if (genericType.genericsList.isEmpty()) {
                    knownGenericInfo[index] = actualType
                    actualType
                } else {
                    die() // bad case
                }
            } else {
                // expect not to replace, shape and first name must match
                if (genericType.type != actualType.type
                        || genericType.genericsList.size != actualType.genericsList.size) {
                    die() // Does not match. This is bad.
                } else {
                    val visitedChildren = genericType.genericsList
                            .zip(actualType.genericsList) { g, a ->
                                copy(genericType = g, actualType = a).visit()
                            }
                    TypeExpr.Identifier(type = genericType.type, genericsList = visitedChildren)
                }
            }
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
            if (genericType.genericsList.isNotEmpty()) {
                die()
            }
            val actualGenericName = genericType.type
            if (actualGenericName != oneGenericDeclaration) {
                die()
            }
            knownGenericInfo[index] = actualType
            actualType
        } else {
            die()
        }
    }

}

/**
 * [inferActualGenericTypeInfo] tries to infer the generic info from [genericDeclarations],
 * [genericType] and [actualType] and tries to reconcile between declaring site and use
 * site
 * It stores the known/inferred info inside [knownGenericInfo], where a `null` value in the array
 * means non-determined actual type expression.
 * If this operation fails, it will throw [GenericTypeInfoDoesNotMatchError].
 */
private fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>, genericType: TypeExpr, actualType: TypeExpr,
        knownGenericInfo: Array<TypeExpr?>
) {
    var typeExpr = genericType
    try {
        for (i in genericDeclarations.indices) {
            val oneGenericDeclaration = genericDeclarations[i]
            typeExpr = PairedTypeExprVisitor(
                    oneGenericDeclaration = oneGenericDeclaration,
                    knownGenericInfo = knownGenericInfo, index = i,
                    genericType = typeExpr, actualType = actualType
            ).visit()
        }
    } catch (e: GenericTypeInfoDoesNotMatchError.InternalError) {
        throw GenericTypeInfoDoesNotMatchError(
                genericDeclarations, genericType, actualType, knownGenericInfo
        )
    }
}

/**
 * [inferActualGenericTypeInfo] tries to infer the generic info and returns from
 * [genericDeclarations], and a list of [genericTypeActualTypePairs].
 * If this operation fails, it will throw [GenericTypeInfoDoesNotMatchError].
 */
internal fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>,
        genericTypeActualTypePairs: Iterable<Pair<TypeExpr, TypeExpr>>
) : List<TypeExpr> {
    val knownInfo = arrayOfNulls<TypeExpr>(size = genericDeclarations.size)
    for ((genericType, actualType) in genericTypeActualTypePairs) {
        inferActualGenericTypeInfo(genericDeclarations, genericType, actualType, knownInfo)
    }
    val knownInfoAsList = knownInfo.filterNotNull()
    if (knownInfoAsList.size != knownInfo.size) {
        throw GenericTypeInfoDoesNotMatchError(
                genericDeclarations = genericDeclarations, knownGenericInfo = knownInfo
        )
    }
    return knownInfoAsList
}

/**
 * [inferActualGenericTypeInfo] tries to infer and return the generic info from
 * [genericDeclarations], [genericType] and [actualType] and tries to reconcile between declaring
 * site and use site.
 * If this operation fails, it will throw [GenericTypeInfoDoesNotMatchError].
 */
internal fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>, genericType: TypeExpr, actualType: TypeExpr
): List<TypeExpr> {
    val knownInfo = arrayOfNulls<TypeExpr>(size = genericDeclarations.size)
    inferActualGenericTypeInfo(genericDeclarations, genericType, actualType, knownInfo)
    val knownInfoAsList = knownInfo.filterNotNull()
    if (knownInfoAsList.size != knownInfo.size) {
        throw GenericTypeInfoDoesNotMatchError(
                genericDeclarations, genericType, actualType, knownInfo
        )
    }
    return knownInfoAsList
}
