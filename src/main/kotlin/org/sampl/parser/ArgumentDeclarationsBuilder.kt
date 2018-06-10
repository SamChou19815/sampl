package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.ArgumentDeclarationsContext as C
import org.sampl.ast.type.TypeExpr as T

/**
 * [ArgumentDeclarationsBuilder] builds argument declarations into AST.
 */
object ArgumentDeclarationsBuilder : PLBaseVisitor<List<Pair<String, T>>>() {

    override fun visitArgumentDeclarations(ctx: C): List<Pair<String, T>> =
            ctx.annotatedVariable().map { c ->
                val text: String = c.LowerIdentifier().text
                val type: T = c.typeAnnotation().typeExprInAnnotation()
                        .accept(TypeExprInAnnotationBuilder)
                text to type
            }

}
