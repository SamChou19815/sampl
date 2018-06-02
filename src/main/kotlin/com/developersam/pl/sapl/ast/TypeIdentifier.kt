package com.developersam.pl.sapl.ast

import kotlin.math.min

/**
 * [TypeIdentifier] is the identifier used in the type declaration, with [name] and some
 * [genericsInfo].
 */
data class TypeIdentifier(
        val name: String, val genericsInfo: List<String> = emptyList()
) : Comparable<TypeIdentifier> {

    override fun compareTo(other: TypeIdentifier): Int {
        val c = name.compareTo(other = other.name)
        if (c != 0) {
            return c
        }
        val l = min(genericsInfo.size, other.genericsInfo.size)
        for (i in 0 until l) {
            val cc = genericsInfo[i].compareTo(other = other.genericsInfo[i])
            if (cc != 0) {
                return cc
            }
        }
        return 0
    }

    override fun toString(): String {
        if (genericsInfo.isEmpty()) {
            return name
        }
        return name + genericsInfo.joinToString(separator = ", ", prefix = "<", postfix = ">")
    }

}
