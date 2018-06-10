package org.sampl.ast.decorated

import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.type.TypeExpr
import org.sampl.codegen.AstToCodeConverter

/**
 * [DecoratedClassFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 * The function [category] defines its behavior during type checking, interpretation, and code
 * generation.
 * It has an additional [type] field.
 */
data class DecoratedClassFunctionMember(
        val category: FunctionCategory, override val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: DecoratedExpression,
        override val type: TypeExpr.Function
) : DecoratedClassMember {

    override val name: String = identifier

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
