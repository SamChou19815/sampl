package com.developersam.pl.sapl.ast

import kotlin.math.min

/**
 * [TypeExpr] represents a set of supported type expression in type annotation.
 */
sealed class TypeExpr : Comparable<TypeExpr> {

    /**
     * [asTypeInformation] converts itself to [TypeInfo] without generics declaration.
     */
    val asTypeInformation: TypeInfo
        get() = TypeInfo(typeExpr = this)

    override fun compareTo(other: TypeExpr): Int {
        if (this is Identifier && other is Identifier) {
            val c = type.compareTo(other = other.type)
            if (c != 0) {
                return c
            }
            val l = min(genericsList.size, other.genericsList.size)
            for (i in 0 until l) {
                val cc = genericsList[i].compareTo(other = other.genericsList[i])
                if (cc != 0) {
                    return cc
                }
            }
            return 0
        } else if (this is TypeExpr.Function && other is TypeExpr.Function) {
            val c = argumentType.compareTo(other = other.argumentType)
            if (c != 0) {
                return c
            }
            return returnType.compareTo(other = other.returnType)
        } else if (this is Identifier && other is TypeExpr.Function) {
            return -1
        } else {
            return 1
        }
    }

    /**
     * [substituteGenerics] uses the given [map] to substitute generic symbols in the type
     * expression with concrete value types.
     */
    abstract fun substituteGenerics(map: Map<String, TypeExpr>): TypeExpr

    /**
     * [Identifier] represents a single [type] with optional [genericsList].
     */
    data class Identifier(
            val type: String, val genericsList: List<TypeExpr> = emptyList()
    ) : TypeExpr() {

        override fun substituteGenerics(map: Map<String, TypeExpr>): TypeExpr =
                map[type].takeIf { genericsList.isEmpty() }
                        ?: Identifier(type, genericsList.map { it.substituteGenerics(map) })

    }

    /**
     * [Function] represents the function type in the type annotation of the form
     * [argumentType] `->` [returnType].
     */
    data class Function(
            val argumentType: TypeExpr, val returnType: TypeExpr
    ) : TypeExpr() {

        override fun substituteGenerics(map: Map<String, TypeExpr>): Function =
                Function(
                        argumentType = argumentType.substituteGenerics(map = map),
                        returnType = returnType.substituteGenerics(map = map)
                )

    }

}
