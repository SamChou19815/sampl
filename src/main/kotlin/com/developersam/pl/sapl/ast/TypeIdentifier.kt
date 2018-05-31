package com.developersam.pl.sapl.ast

import kotlin.math.min

/**
 * [TypeIdentifier] is the AST for the type identifier node.
 *
 * @param type name of the type.
 * @param genericsList generics declaration.
 */
internal data class TypeIdentifier(
        val type: String,
        val genericsList: List<TypeIdentifier> = emptyList()
) : Comparable<TypeIdentifier> {

    override fun compareTo(other: TypeIdentifier): Int {
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
    }

    fun substituteGenerics(map: Map<String, TypeExprInAnnotation>): TypeIdentifier {
        TODO("not implemented")
    }

}
