package com.developersam.pl.sapl.ast

/**
 * [TypeExprInDeclaration] represents a set of supported type expression in type declaration.
 */
internal sealed class TypeExprInDeclaration

/**
 * [VariantTypeInDeclaration] represents the constructor and an optional associated type all defined
 * in [map].
 */
internal data class VariantTypeInDeclaration(
        val map: Map<String, TypeExprInAnnotation?>
) : TypeExprInDeclaration()

/**
 * [StructTypeInDeclaration] represents the struct declaration with all those info defined in [map].
 */
internal data class StructTypeInDeclaration(
        val map: Map<String, TypeExprInAnnotation>
) : TypeExprInDeclaration()