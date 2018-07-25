package org.sampl.ast.type

/**
 * [TypeDeclaration] represents a set of supported type expression in type declaration.
 */
sealed class TypeDeclaration {

    /**
     * [isEmpty] reports whether the declaration is an empty struct.
     */
    val isEmpty: Boolean
        get() = when (this) {
            is Variant -> false
            is Struct -> map.isEmpty()
        }

    /**
     * [Variant] represents the constructor and an optional associated type all defined in [map].
     *
     * @property map the map from variant identifiers to types.
     */
    data class Variant(val map: Map<String, TypeExpr?>) : TypeDeclaration()

    /**
     * [Struct] represents the struct declaration with all those info defined in [map].
     *
     * @property map the map from struct member identifiers to types.
     */
    data class Struct(val map: Map<String, TypeExpr>) : TypeDeclaration()

}
