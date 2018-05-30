package com.developersam.pl.sapl.ast

/**
 * [M] is short for the substitution table.
 */
private typealias M = Map<String, TypeExprInAnnotation>

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
     * [substituteGenericInfo] uses the given [map] to substitute generic symbols in the type
     * expression with concrete value types.
     */
    abstract fun substituteGenericInfo(map: M): TypeExprInAnnotation

}

/**
 * [SingleIdentifierTypeInAnnotation] represents a single type [identifier] in the type annotation.
 */
internal data class SingleIdentifierTypeInAnnotation(
        val identifier: TypeIdentifier
) : TypeExprInAnnotation() {

    override fun substituteGenericInfo(map: M): SingleIdentifierTypeInAnnotation {
        TODO("not implemented")
    }

}

/**
 * [FunctionTypeInAnnotation] represents the function type in the type annotation of the form
 * [argumentType] `->` [returnType].
 */
internal data class FunctionTypeInAnnotation(
        val argumentType: TypeExprInAnnotation,
        val returnType: TypeExprInAnnotation
) : TypeExprInAnnotation() {

    override fun substituteGenericInfo(map: M): FunctionTypeInAnnotation =
            FunctionTypeInAnnotation(
                    argumentType = argumentType.substituteGenericInfo(map = map),
                    returnType = returnType.substituteGenericInfo(map = map)
            )

}
