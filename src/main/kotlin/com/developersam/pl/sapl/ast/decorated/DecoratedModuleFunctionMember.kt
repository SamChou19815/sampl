package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.TypeExpr

/**
 * [DecoratedModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 * It has an additional [type] field.
 */
data class DecoratedModuleFunctionMember(
        override val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: DecoratedExpression,
        override val type: TypeExpr.Function
) : DecoratedModuleMember {

    override val name: String = identifier

    override fun toString(): String {
        val publicPart = if (isPublic) "" else "private "
        val genericsPart = if (genericsDeclaration.isEmpty()) "" else {
            genericsDeclaration.joinToString(separator = ", ", prefix = "<", postfix = ">")
        }
        val argumentPart = arguments.joinToString(separator = " ") { (n, t) -> "($n: $t)" }
        return "${publicPart}let $identifier $genericsPart $argumentPart : $returnType = $body"
    }

}