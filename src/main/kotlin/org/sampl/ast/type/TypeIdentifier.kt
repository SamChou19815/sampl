package org.sampl.ast.type

import org.sampl.util.joinToGenericsInfoString
import kotlin.math.min

/**
 * [TypeIdentifier] is the identifier used in the type declaration, with [name] and some
 * [genericsInfo].
 *
 * @property name the name of type.
 * @property genericsInfo a list of generics declared associated with the type, if any.
 */
internal data class TypeIdentifier(
        val name: String, val genericsInfo: List<String> = emptyList()
) : Comparable<TypeIdentifier> {

    /**
     * Compare two [TypeIdentifier].
     */
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

    /**
     * Returns the string representation of the type.
     */
    override fun toString(): String =
            if (genericsInfo.isEmpty()) name else {
                name + genericsInfo.joinToGenericsInfoString()
            }

}
