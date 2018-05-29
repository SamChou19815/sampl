package com.developersam.pl.sapl.ast

/**
 * [TypeIdentifier] is the AST for the type identifier node.
 *
 * @param type name of the type.
 * @param genericsList generics declaration.
 */
internal data class TypeIdentifier(
        val moduleChain: List<String> = emptyList(), val type: String,
        val genericsList: List<TypeIdentifier> = emptyList()
) : Comparable<TypeIdentifier> {

    override fun compareTo(other: TypeIdentifier): Int {
        val c = type.compareTo(other = other.type)
        when {
            c != 0 -> return c
            moduleChain.size < other.moduleChain.size -> return -1
            moduleChain.size > other.moduleChain.size -> return 1
            else -> {
                val l = moduleChain.size
                for (i in 0 until l) {
                    val cc = moduleChain[i].compareTo(other = other.moduleChain[i])
                    if (cc != 0) {
                        return cc
                    }
                }
                return 0
            }
        }
    }

}
