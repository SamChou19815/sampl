package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.TypeExpr

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

    override fun toString(): String =
            "${if (isPublic) "" else "private "}let $identifier: $type = $expr"

}
