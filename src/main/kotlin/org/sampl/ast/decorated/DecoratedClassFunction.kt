package org.sampl.ast.decorated

import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.type.TypeExpr
import org.sampl.codegen.AstToCodeConverter
import org.sampl.codegen.CodeConvertible

/**
 * [DecoratedClassFunction] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 * The function [category] defines its behavior during type checking, interpretation, and code
 * generation.
 * It has an additional [type] field.
 *
 * @property category category of the function.
 * @property isPublic whether the function is public.
 * @property identifier the identifier for the function.
 * @property genericsDeclaration the generics declaration.
 * @property arguments a list of arguments passed to the function.
 * @property returnType type of the return value.
 * @property body body part of the function.
 * @property type of the entire function.
 */
data class DecoratedClassFunction(
        val category: FunctionCategory, val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: DecoratedExpression,
        val type: TypeExpr.Function
) : CodeConvertible {

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
