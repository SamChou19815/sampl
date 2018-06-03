package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.TranspilerVisitor
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.codegen.IndentationQueue

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

    override fun prettyPrint(q: IndentationQueue) {
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
                    .append(arguments.joinToString(separator = " ") { (n, t) -> "($n: $t)" })
                    .append(" : ").append(returnType.toString())
                    .append(" =")
        }.toString()
        q.addLine(line = header)
        q.indentAndApply { body.prettyPrintOrInline(q = this) }
    }

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, functionMember = this)

}