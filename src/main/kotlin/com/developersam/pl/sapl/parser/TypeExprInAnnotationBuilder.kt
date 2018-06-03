package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.ast.type.TypeExpr
import org.antlr.v4.runtime.tree.TerminalNode
import com.developersam.pl.sapl.antlr.PLParser.FunctionTypeInAnnotationContext as Func;
import com.developersam.pl.sapl.antlr.PLParser.NestedTypeInAnnotationContext as Nested
import com.developersam.pl.sapl.antlr.PLParser.SingleIdentifierTypeInAnnotationContext as Single

/**
 * [TypeExprInAnnotationBuilder] builds type annotation AST from parse tree.
 */
internal object TypeExprInAnnotationBuilder : PLBaseVisitor<TypeExpr>() {

    override fun visitNestedTypeInAnnotation(ctx: Nested): TypeExpr =
            ctx.typeExprInAnnotation().accept(this)

    override fun visitSingleIdentifierTypeInAnnotation(ctx: Single): TypeExpr {
        val type = ctx.UpperIdentifier().joinToString(
                separator = ".", transform = TerminalNode::getText
        )
        val genericsList = ctx.genericsSpecialization()?.typeExprInAnnotation()
                ?.map { it.accept(this) } ?: emptyList()
        return TypeExpr.Identifier(type = type, genericsInfo = genericsList)
    }

    override fun visitFunctionTypeInAnnotation(ctx: Func): TypeExpr =
            TypeExpr.Function(
                    argumentType = ctx.typeExprInAnnotation(0).accept(this),
                    returnType = ctx.typeExprInAnnotation(1).accept(this)
            )

}
