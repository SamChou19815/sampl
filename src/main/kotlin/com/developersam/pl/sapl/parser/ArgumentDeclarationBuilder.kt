package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ArgumentDeclarationContext
import com.developersam.pl.sapl.ast.TypeExprInAnnotation as TA

/**
 * [ArgumentDeclarationBuilder] builds argument declaration into AST.
 */
internal object ArgumentDeclarationBuilder : PLBaseVisitor<Pair<String, TA>>() {

    override fun visitArgumentDeclaration(ctx: ArgumentDeclarationContext): Pair<String, TA> {
        val a = ctx.annotatedVariable()
        val text = a.text
        val type = a.typeAnnotation().typeExprInAnnotation()
                .accept(TypeExprInAnnotationBuilder)
        return text to type
    }

}