package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.codegen.TranspilerVisitor
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.codegen.IndentationQueue

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

    override fun prettyPrint(q: IndentationQueue) {
        val header = StringBuilder().apply {
            if (!isPublic) {
                append("private ")
            }
            append("let ").append(identifier).append(" =")
        }.toString()
        q.addLine(line = header)
        q.indentAndApply { expr.prettyPrintOrInline(q = this) }
    }

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, constantMember = this)

}
