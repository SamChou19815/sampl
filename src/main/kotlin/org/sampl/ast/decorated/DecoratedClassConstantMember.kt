package org.sampl.ast.decorated

import org.sampl.ast.type.TypeExpr
import org.sampl.codegen.AstToCodeConverter

/**
 * [DecoratedClassConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 * It has an additional [type] field.
 */
data class DecoratedClassConstantMember(
        override val isPublic: Boolean, val identifier: String, val expr: DecoratedExpression,
        override val type: TypeExpr
) : DecoratedClassMember {

    override val name: String = identifier

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
