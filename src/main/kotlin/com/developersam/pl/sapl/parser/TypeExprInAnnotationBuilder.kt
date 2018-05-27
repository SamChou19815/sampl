package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.ast.FunctionTypeInAnnotation
import com.developersam.pl.sapl.ast.SingleIdentifierTypeInAnnotation
import com.developersam.pl.sapl.antlr.PLParser.FunctionTypeInAnnotationContext as Func;
import com.developersam.pl.sapl.antlr.PLParser.NestedTypeInAnnotationContext as Nested
import com.developersam.pl.sapl.antlr.PLParser.SingleIdentifierTypeInAnnotationContext as Single
import com.developersam.pl.sapl.ast.TypeExprInAnnotation as T

/**
 * [TypeExprInAnnotationBuilder] builds type annotation AST from parse tree.
 */
internal object TypeExprInAnnotationBuilder : PLBaseVisitor<T>() {

    override fun visitNestedTypeInAnnotation(ctx: Nested): T =
            ctx.typeExprInAnnotation().accept(this)

    override fun visitSingleIdentifierTypeInAnnotation(ctx: Single): T =
            SingleIdentifierTypeInAnnotation(
                    identifier = ctx.typeIdentifier().accept(TypeIdentifierBuilder)
            )

    override fun visitFunctionTypeInAnnotation(ctx: Func): T =
            FunctionTypeInAnnotation(
                    argumentType = ctx.typeExprInAnnotation(0).accept(this),
                    returnType = ctx.typeExprInAnnotation(1).accept(this)
            )

}
