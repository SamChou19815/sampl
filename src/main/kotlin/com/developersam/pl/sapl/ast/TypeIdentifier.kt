package com.developersam.pl.sapl.ast

/**
 * [TypeIdentifier] is the AST for the type identifier node.
 *
 * @param type name of the type.
 * @param genericsList generics declaration.
 */
data class TypeIdentifier(val type: String, val genericsList: List<TypeIdentifier> = emptyList())
