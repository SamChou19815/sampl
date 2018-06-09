package org.sampl.ast.decorated

import org.sampl.ast.type.TypeExpr
import org.sampl.codegen.IdtQueue
import org.sampl.codegen.TranspilerVisitor

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

    override fun prettyPrint(q: IdtQueue) {
        val header = StringBuilder().apply {
            if (!isPublic) {
                append("private ")
            }
            append("let ").append(identifier).append(" =")
        }.toString()
        q.addLine(line = header)
        q.indentAndApply { expr.prettyPrintOrInline(q = this) }
    }

    override fun acceptTranspilation(q: IdtQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, constantMember = this)

}
