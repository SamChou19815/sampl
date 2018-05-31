package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.TypeExprInAnnotation

/**
 * [DecoratedModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 * It has an additional [type] field.
 */
internal data class DecoratedModuleFunctionMember(
        val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: DecoratedExpression,
        override val type: TypeExprInAnnotation.Function
) : DecoratedModuleMember {

    override val name: String = identifier

}