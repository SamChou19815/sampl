package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.TypeExprInAnnotation

/**
 * [DecoratedModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 * It has an additional [type] field.
 */
internal data class DecoratedModuleConstantMember(
        val isPublic: Boolean, val identifier: String, val expr: DecoratedExpression,
        override val type: TypeExprInAnnotation
) : DecoratedModuleMember {
    override val name: String = identifier
}
