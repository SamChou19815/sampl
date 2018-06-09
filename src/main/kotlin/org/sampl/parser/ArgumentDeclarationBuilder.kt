package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.ArgumentDeclarationContext
import org.sampl.ast.type.TypeExpr

/**
 * [ArgumentDeclarationBuilder] builds argument declaration into AST.
 */
internal object ArgumentDeclarationBuilder : PLBaseVisitor<Pair<String, TypeExpr>>() {

    override fun visitArgumentDeclaration(ctx: ArgumentDeclarationContext): Pair<String, TypeExpr> {
        val a = ctx.annotatedVariable()
        val text = a.LowerIdentifier().text
        val type = a.typeAnnotation().typeExprInAnnotation()
                .accept(TypeExprInAnnotationBuilder)
        return text to type
    }

}