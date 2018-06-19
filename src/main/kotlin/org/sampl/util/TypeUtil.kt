package org.sampl.util

import org.sampl.ast.type.TypeExpr
import org.sampl.exceptions.GenericsError

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
    @Throws(GenericsError.GenericsTypeInfoDoesNotMatch.InternalError::class)
    private fun die(): Nothing = throw GenericsError.GenericsTypeInfoDoesNotMatch.InternalError()

    /**
     * [visit] visits the pairs and try to reconcile between declared and actual.
     *
     * @return the type expression of [genericType] with known generic info replaced with
     * actual ones.
     */
    @Throws(GenericsError.GenericsTypeInfoDoesNotMatch.InternalError::class)
    fun visit(): TypeExpr {
        return if (genericType is TypeExpr.Identifier && actualType is TypeExpr.Identifier) {
            if (genericType.type == oneGenericDeclaration) {
                // Name matches the generic name, expect to replace!
                if (genericType.genericsInfo.isEmpty()) {
                    knownGenericInfo[index] = actualType
                    actualType
                } else {
                    die() // bad case
                }
            } else {
                // expect not to replace, shape and first name must match
                if (genericType.type != actualType.type
                        || genericType.genericsInfo.size != actualType.genericsInfo.size) {
                    die() // Does not match. This is bad.
                } else {
                    val visitedChildren = genericType.genericsInfo
                            .zip(actualType.genericsInfo) { g, a ->
                                copy(genericType = g, actualType = a).visit()
                            }
                    TypeExpr.Identifier(type = genericType.type, genericsInfo = visitedChildren)
                }
            }
        } else if (genericType is TypeExpr.Function && actualType is TypeExpr.Function) {
            if (genericType.argumentTypes.size != actualType.argumentTypes.size) {
                die()
            }
            val partiallyKnownArgumentTypes = genericType.argumentTypes
                    .zip(actualType.argumentTypes).map { (g, a) ->
                        copy(genericType = g, actualType = a).visit()
                    }
            val partiallyKnownReturnType = copy(
                    genericType = genericType.returnType,
                    actualType = actualType.returnType
            ).visit()
            TypeExpr.Function(
                    argumentTypes = partiallyKnownArgumentTypes,
                    returnType = partiallyKnownReturnType
            )
        } else if (genericType is TypeExpr.Identifier && actualType is TypeExpr.Function) {
            if (genericType.genericsInfo.isNotEmpty()) {
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
 * site at [lineNo].
 * It stores the known/inferred info inside [knownGenericInfo], where a `null` value in the array
 * means non-determined actual type expression.
 * If this operation fails, it will throw [GenericsError.GenericsTypeInfoDoesNotMatch].
 */
private fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>, genericType: TypeExpr, actualType: TypeExpr,
        knownGenericInfo: Array<TypeExpr?>, lineNo: Int
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
    } catch (e: GenericsError.GenericsTypeInfoDoesNotMatch.InternalError) {
        throw GenericsError.GenericsTypeInfoDoesNotMatch(
                lineNo, genericDeclarations, genericType, actualType, knownGenericInfo
        )
    }
}

/**
 * [inferActualGenericTypeInfo] tries to infer the generic info and returns from
 * [genericDeclarations], and a list of [genericTypeActualTypePairs] at [lineNo].
 * If this operation fails, it will throw [GenericsError.GenericsTypeInfoDoesNotMatch].
 */
internal fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>,
        genericTypeActualTypePairs: Iterable<Pair<TypeExpr, TypeExpr>>, lineNo: Int
): List<TypeExpr> {
    val knownInfo = arrayOfNulls<TypeExpr>(size = genericDeclarations.size)
    for ((genericType, actualType) in genericTypeActualTypePairs) {
        inferActualGenericTypeInfo(genericDeclarations, genericType, actualType, knownInfo, lineNo)
    }
    val knownInfoAsList = knownInfo.filterNotNull()
    if (knownInfoAsList.size != knownInfo.size) {
        throw GenericsError.GenericsTypeInfoDoesNotMatch(
                lineNo = lineNo,
                genericDeclarations = genericDeclarations, knownGenericInfo = knownInfo
        )
    }
    return knownInfoAsList
}

/**
 * [inferActualGenericTypeInfo] tries to infer and return the generic info from
 * [genericDeclarations], [genericType] and [actualType] and tries to reconcile between declaring
 * site and use site at [lineNo].
 * If this operation fails, it will throw [GenericsError.GenericsTypeInfoDoesNotMatch].
 */
internal fun inferActualGenericTypeInfo(
        genericDeclarations: List<String>, genericType: TypeExpr, actualType: TypeExpr, lineNo: Int
): List<TypeExpr> {
    val knownInfo = arrayOfNulls<TypeExpr>(size = genericDeclarations.size)
    inferActualGenericTypeInfo(genericDeclarations, genericType, actualType, knownInfo, lineNo)
    val knownInfoAsList = knownInfo.filterNotNull()
    if (knownInfoAsList.size != knownInfo.size) {
        throw GenericsError.GenericsTypeInfoDoesNotMatch(
                lineNo, genericDeclarations, genericType, actualType, knownInfo
        )
    }
    return knownInfoAsList
}
