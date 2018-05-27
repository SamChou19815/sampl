package com.developersam.pl.sapl.ast

/**
 * [TypeExprInAnnotation] represents a set of supported type expression in type annotation.
 */
internal sealed class TypeExprInAnnotation

/**
 * [SingleIdentifierTypeInAnnotation] represents a single type [identifier] in the type annotation.
 */
internal data class SingleIdentifierTypeInAnnotation(
        val identifier: TypeIdentifier
) : TypeExprInAnnotation()

/**
 * [FunctionTypeInAnnotation] represents the function type in the type annotation of the form
 * [argumentType] `->` [returnType].
 */
internal data class FunctionTypeInAnnotation(
        val argumentType: TypeExprInAnnotation,
        val returnType: TypeExprInAnnotation
) : TypeExprInAnnotation()
