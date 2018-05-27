package com.developersam.pl.sapl.ast

/**
 * [TypeExprInAnnotation] represents a set of supported type expression in type annotation.
 */
sealed class TypeExprInAnnotation : AstNode {

    final override fun <T> accept(visitor: AstVisitor<T>): T =
            visitor.visit(typeExprInAnnotation = this)

}

/**
 * [SingleIdentifierTypeInAnnotation] represents a single type [identifier] in the type annotation.
 */
data class SingleIdentifierTypeInAnnotation(val identifier: TypeIdentifier) : TypeExprInAnnotation()

/**
 * [FunctionTypeInAnnotation] represents the function type in the type annotation of the form
 * [argumentType] `->` [returnType].
 */
data class FunctionTypeInAnnotation(
        val argumentType: TypeExprInAnnotation,
        val returnType: TypeExprInAnnotation
) : TypeExprInAnnotation()
