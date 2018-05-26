package com.developersam.pl.sapl.ast

/**
 * [TypeExprInAnnotation] represents a set of supported type expression in type annotation.
 */
sealed class TypeExprInAnnotation

/**
 * [SingleIdentifierTypeInAnnotation] represents a single type identifier in the type annotation.
 *
 * @param identifier the single type identifier.
 */
data class SingleIdentifierTypeInAnnotation(val identifier: TypeIdentifier) : TypeExprInAnnotation()

/**
 * [FunctionTypeInAnnotation] represents the function type in the type annotation.
 *
 * @param argumentType the type of the argument.
 * @param returnType the type of the return value.
 */
data class FunctionTypeInAnnotation(
        val argumentType: TypeExprInAnnotation,
        val returnType: TypeExprInAnnotation
) : TypeExprInAnnotation()
