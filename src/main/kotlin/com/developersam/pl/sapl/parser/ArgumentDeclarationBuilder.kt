package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ArgumentDeclarationContext
import com.developersam.pl.sapl.ast.TypeExprInAnnotation as TA
import com.developersam.pl.sapl.ast.UnitNode

/**
 * [ArgumentDeclarationBuilder] builds argument declaration into AST.
 */
object ArgumentDeclarationBuilder : PLBaseVisitor<Pair<String, TA>>() {

    override fun visitArgumentDeclaration(ctx: ArgumentDeclarationContext): Pair<String, TA> =
            if (ctx.UNIT() != null) {
                UnitNode.annotatedUnit
            } else {
                val a = ctx.annotatedVariable()
                val text = a.text
                val type = a.typeAnnotation().typeExprInAnnotation()
                        .accept(TypeExprInAnnotationBuilder)
                text to type
            }

}