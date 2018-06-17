package org.sampl.parser

import org.antlr.v4.runtime.tree.TerminalNode
import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.BitExprContext
import org.sampl.antlr.PLParser.ComparisonExprContext
import org.sampl.antlr.PLParser.ConjunctionExprContext
import org.sampl.antlr.PLParser.ConstructorExprContext
import org.sampl.antlr.PLParser.DisjunctionExprContext
import org.sampl.antlr.PLParser.FactorExprContext
import org.sampl.antlr.PLParser.FunExprContext
import org.sampl.antlr.PLParser.FunctionApplicationExprContext
import org.sampl.antlr.PLParser.IdentifierExprContext
import org.sampl.antlr.PLParser.IfElseExprContext
import org.sampl.antlr.PLParser.LetExprContext
import org.sampl.antlr.PLParser.LiteralExprContext
import org.sampl.antlr.PLParser.MatchExprContext
import org.sampl.antlr.PLParser.NestedExprContext
import org.sampl.antlr.PLParser.NotExprContext
import org.sampl.antlr.PLParser.StructMemberAccessExprContext
import org.sampl.antlr.PLParser.TermExprContext
import org.sampl.antlr.PLParser.ThrowExprContext
import org.sampl.antlr.PLParser.TryCatchExprContext
import org.sampl.ast.common.BinaryOperator
import org.sampl.ast.common.Literal
import org.sampl.ast.raw.BinaryExpr
import org.sampl.ast.raw.Expression
import org.sampl.ast.raw.FunctionApplicationExpr
import org.sampl.ast.raw.FunctionExpr
import org.sampl.ast.raw.IfElseExpr
import org.sampl.ast.raw.LetExpr
import org.sampl.ast.raw.LiteralExpr
import org.sampl.ast.raw.MatchExpr
import org.sampl.ast.raw.NotExpr
import org.sampl.ast.raw.StructMemberAccessExpr
import org.sampl.ast.raw.ThrowExpr
import org.sampl.ast.raw.TryCatchExpr
import org.sampl.ast.raw.VariableIdentifierExpr

/**
 * [ExprBuilder] builds expression AST from parse tree.
 */
internal object ExprBuilder : PLBaseVisitor<Expression>() {

    override fun visitNestedExpr(ctx: NestedExprContext): Expression =
            ctx.expression().accept(this)

    override fun visitLiteralExpr(ctx: LiteralExprContext): Expression =
            LiteralExpr(literal = Literal.from(text = ctx.literal().text))

    override fun visitIdentifierExpr(ctx: IdentifierExprContext): Expression {
        val upperIds = ctx.UpperIdentifier()
        val lower = ctx.LowerIdentifier().text
        val variable = if (upperIds.isEmpty()) lower else {
            upperIds.joinToString(
                    separator = ".", postfix = ".", transform = TerminalNode::getText
            ) + lower
        }
        val genericInfo = ctx.genericsSpecialization()
                ?.typeExprInAnnotation()
                ?.map { it.accept(TypeExprInAnnotationBuilder) }
                ?: emptyList()
        return VariableIdentifierExpr(variable = variable, genericInfo = genericInfo)
    }

    override fun visitConstructorExpr(ctx: ConstructorExprContext): Expression =
            ctx.accept(ConstructorExprBuilder)

    override fun visitStructMemberAccessExpr(ctx: StructMemberAccessExprContext): Expression =
            StructMemberAccessExpr(
                    structExpr = ctx.expression().accept(this),
                    memberName = ctx.LowerIdentifier().text
            )

    override fun visitNotExpr(ctx: NotExprContext): Expression =
            NotExpr(expr = ctx.expression().accept(this))

    override fun visitBitExpr(ctx: BitExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.bitOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitFactorExpr(ctx: FactorExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.factorOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitTermExpr(ctx: TermExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.termOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitComparisonExpr(ctx: ComparisonExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.comparisonOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitConjunctionExpr(ctx: ConjunctionExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.AND,
                    right = ctx.expression(1).accept(this)
            )

    override fun visitDisjunctionExpr(ctx: DisjunctionExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.OR,
                    right = ctx.expression(1).accept(this)
            )

    override fun visitThrowExpr(ctx: ThrowExprContext): Expression =
            ThrowExpr(
                    type = ctx.typeExprInAnnotation().accept(TypeExprInAnnotationBuilder),
                    expr = ctx.expression().accept(this)
            )

    override fun visitIfElseExpr(ctx: IfElseExprContext): Expression =
            IfElseExpr(
                    condition = ctx.expression(0).accept(this),
                    e1 = ctx.expression(1).accept(this),
                    e2 = ctx.expression(2).accept(this)
            )

    override fun visitMatchExpr(ctx: MatchExprContext): Expression =
            MatchExpr(
                    exprToMatch = ctx.expression().accept(this),
                    matchingList = ctx.patternToExpr().map { c ->
                        val pattern = c.pattern().accept(PatternBuilder)
                        val expr = c.expression().accept(this)
                        pattern to expr
                    }
            )

    override fun visitFunExpr(ctx: FunExprContext): Expression =
            FunctionExpr(
                    arguments = ctx.argumentDeclarations().accept(ArgumentDeclarationsBuilder),
                    body = ctx.expression().accept(this)
            )

    override fun visitFunctionApplicationExpr(ctx: FunctionApplicationExprContext): Expression {
        val exprContextList = ctx.expression()
        val functionExpr = exprContextList[0].accept(this)
        val arguments = arrayListOf<Expression>()
        for (i in 1 until exprContextList.size) {
            arguments.add(exprContextList[i].accept(this))
        }
        return FunctionApplicationExpr(functionExpr, arguments)
    }

    override fun visitTryCatchExpr(ctx: TryCatchExprContext): Expression =
            TryCatchExpr(
                    tryExpr = ctx.expression(0).accept(this),
                    exception = ctx.LowerIdentifier().text,
                    catchHandler = ctx.expression(1).accept(this)
            )

    override fun visitLetExpr(ctx: LetExprContext): Expression =
            LetExpr(
                    identifier = ctx.LowerIdentifier().text,
                    e1 = ctx.expression(0).accept(this),
                    e2 = ctx.expression(1).accept(this)
            )

}
