package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.config.IndentationStrategy

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

    override fun prettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder)
        if (!isPublic) {
            builder.append("private ")
        }
        builder.append("let ")
        if (genericsDeclaration.isNotEmpty()) {
            builder.append(genericsDeclaration.joinToString(
                    separator = ", ", prefix = "<", postfix = "> "
            ))
        }
        builder.append(identifier).append(' ')
        builder.append(arguments.joinToString(separator = " ") { (n, t) -> "($n: $t)" })
                .append(" : ")
        returnType.prettyPrint(builder = builder)
        builder.append(" =\n")
        body.prettyPrint(level = level + 1, builder = builder)
    }

}