package com.developersam.pl.sapl.ast

/**
 * [TypeDeclaration] represents a set of supported type expression in type declaration.
 */
sealed class TypeDeclaration {

    /**
     * [Variant] represents the constructor and an optional associated type all defined in [map].
     */
    data class Variant(
            val map: Map<String, TypeExpr?>
    ) : TypeDeclaration()

    /**
     * [Struct] represents the struct declaration with all those info defined in [map].
     */
    data class Struct(val map: Map<String, TypeExpr>) : TypeDeclaration()

}
