package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.ast.type.TypeExpr
import org.antlr.v4.runtime.tree.TerminalNode
import org.sampl.antlr.PLParser.FunctionTypeInAnnotationContext as Func
import org.sampl.antlr.PLParser.SingleIdentifierTypeInAnnotationContext as Single

/**
 * [TypeExprInAnnotationBuilder] builds type annotation AST from parse tree.
 */
internal object TypeExprInAnnotationBuilder : PLBaseVisitor<TypeExpr>() {

    override fun visitSingleIdentifierTypeInAnnotation(ctx: Single): TypeExpr {
        val type = ctx.UpperIdentifier().joinToString(
                separator = ".", transform = TerminalNode::getText
        )
        val genericsList = ctx.genericsSpecialization()?.typeExprInAnnotation()
                ?.map { it.accept(this) } ?: emptyList()
        return TypeExpr.Identifier(type = type, genericsInfo = genericsList)
    }

    override fun visitFunctionTypeInAnnotation(ctx: Func): TypeExpr {
        val types = ctx.typeExprInAnnotation()
        val len = types.size
        return TypeExpr.Function(
                argumentTypes = types.subList(fromIndex = 0, toIndex = len - 1)
                        .map { it.accept(this) },
                returnType = types[len - 1].accept(this)
        )
    }

}
