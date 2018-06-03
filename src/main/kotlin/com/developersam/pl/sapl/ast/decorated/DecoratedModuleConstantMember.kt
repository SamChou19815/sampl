package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.config.IndentationStrategy

/**
 * [DecoratedModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 * It has an additional [type] field.
 */
data class DecoratedModuleConstantMember(
        override val isPublic: Boolean, val identifier: String, val expr: DecoratedExpression,
        override val type: TypeExpr
) : DecoratedModuleMember {

    override val name: String = identifier

    override fun prettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder)
        if (!isPublic) {
            builder.append("private ")
        }
        builder.append("let ").append(identifier).append(" =\n")
        expr.prettyPrint(level = level + 1, builder = builder)
    }

    override fun toString(): String =
            "${if (isPublic) "" else "private "}let $identifier: $type = $expr"

}
