package org.sampl.ast.decorated

import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.codegen.IdtQueue
import org.sampl.codegen.TranspilerVisitor

/**
 * [DecoratedClassFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 * It has an additional [type] field.
 */
data class DecoratedClassFunctionMember(
        override val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: DecoratedExpression,
        override val type: TypeExpr.Function
) : DecoratedClassMember {

    override val name: String = identifier

    override fun prettyPrint(q: IdtQueue) {
        val header = StringBuilder().apply {
            if (!isPublic) {
                append("private ")
            }
            append("let ")
            if (genericsDeclaration.isNotEmpty()) {
                append(genericsDeclaration.joinToString(
                        separator = ", ", prefix = "<", postfix = "> "
                ))
            }
            append(identifier).append(' ')
                    .append(arguments.joinToString(separator = " ") { (n, t) ->
                        if (n == "_unit_" && t == unitTypeExpr) {
                            "()"
                        } else {
                            "($n: $t)"
                        }
                    })
                    .append(" : ").append(returnType.toString())
                    .append(" =")
        }.toString()
        q.addLine(line = header)
        q.indentAndApply { body.prettyPrintOrInline(q = this) }
    }

    override fun acceptTranspilation(q: IdtQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, functionMember = this)

}