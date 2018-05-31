package com.developersam.pl.sapl.ast

/**
 * [TypeExprInAnnotation] represents a set of supported type expression in type annotation.
 */
internal sealed class TypeExprInAnnotation {

    /**
     * [asTypeInformation] converts itself to [TypeInformation] without generics declaration.
     */
    val asTypeInformation: TypeInformation
        get() = TypeInformation(typeExpr = this)

    /**
     * [substituteGenerics] uses the given [map] to substitute generic symbols in the type
     * expression with concrete value types.
     */
    abstract fun substituteGenerics(map: Map<String, TypeExprInAnnotation>): TypeExprInAnnotation

    /**
     * [SingleIdentifier] represents a single type [identifier] in the type
     * annotation.
     */
    internal data class SingleIdentifier(
            val identifier: TypeIdentifier
    ) : TypeExprInAnnotation() {

        override fun substituteGenerics(map: Map<String, TypeExprInAnnotation>): SingleIdentifier =
                SingleIdentifier(identifier.substituteGenerics(map = map))

    }

    /**
     * [Function] represents the function type in the type annotation of the form
     * [argumentType] `->` [returnType].
     */
    internal data class Function(
            val argumentType: TypeExprInAnnotation,
            val returnType: TypeExprInAnnotation
    ) : TypeExprInAnnotation() {

        override fun substituteGenerics(map: Map<String, TypeExprInAnnotation>): Function =
                Function(
                        argumentType = argumentType.substituteGenerics(map = map),
                        returnType = returnType.substituteGenerics(map = map)
                )

    }

}
