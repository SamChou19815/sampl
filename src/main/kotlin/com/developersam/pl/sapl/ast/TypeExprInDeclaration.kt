package com.developersam.pl.sapl.ast

/**
 * [TypeExprInDeclaration] represents a set of supported type expression in type declaration.
 */
internal sealed class TypeExprInDeclaration {

    /**
     * [Variant] represents the constructor and an optional associated type all defined in [map].
     */
    internal data class Variant(
            val map: Map<String, TypeExprInAnnotation?>
    ) : TypeExprInDeclaration()

    /**
     * [Struct] represents the struct declaration with all those info defined in [map].
     */
    internal data class Struct(val map: Map<String, TypeExprInAnnotation>) : TypeExprInDeclaration()

}
