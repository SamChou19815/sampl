package com.developersam.pl.sapl.ast

/**
 * [TypeExprInDeclaration] represents a set of supported type expression in type declaration.
 */
sealed class TypeExprInDeclaration : AstNode {

    final override fun <T> accept(visitor: AstVisitor<T>): T =
            visitor.visit(typeExprInDeclaration = this)

}

/**
 * [VariantTypeInDeclaration] represents the constructor and an optional associated type all defined
 * in [map].
 */
data class VariantTypeInDeclaration(
        val map: Map<String, TypeExprInAnnotation?>
) : TypeExprInDeclaration()

/**
 * [StructTypeInDeclaration] represents the struct declaration with all those info defined in [map].
 */
data class StructTypeInDeclaration(
        val map: Map<String, TypeExprInAnnotation>
) : TypeExprInDeclaration()
