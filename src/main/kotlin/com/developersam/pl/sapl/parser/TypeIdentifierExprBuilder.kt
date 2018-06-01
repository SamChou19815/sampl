package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.SingleIdentifierTypeInAnnotationContext as C
import com.developersam.pl.sapl.ast.TypeExpr
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [TypeIdentifierExprBuilder] builds single type identifier AST from parse tree.
 */
object TypeIdentifierExprBuilder : PLBaseVisitor<TypeExpr.Identifier>() {

    override fun visitSingleIdentifierTypeInAnnotation(ctx: C): TypeExpr.Identifier {
        val c = ctx.typeIdentifier()
        val type = c.UpperIdentifier().joinToString(
                separator = ".", transform = TerminalNode::getText
        )
        val genericsList = c.genericsBracket().typeExprInAnnotation().map { it.accept(this) }
        return TypeExpr.Identifier(type = type, genericsList = genericsList)
    }

}