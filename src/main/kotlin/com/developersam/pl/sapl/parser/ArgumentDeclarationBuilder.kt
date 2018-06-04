package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ArgumentDeclarationContext
import com.developersam.pl.sapl.ast.type.TypeExpr

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