package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.BitExprContext
import com.developersam.pl.sapl.antlr.PLParser.BooleanExprContext
import com.developersam.pl.sapl.antlr.PLParser.ComparisonExprContext
import com.developersam.pl.sapl.antlr.PLParser.FactorExprContext
import com.developersam.pl.sapl.antlr.PLParser.FunExprContext
import com.developersam.pl.sapl.antlr.PLParser.FunctionApplicationExprContext
import com.developersam.pl.sapl.antlr.PLParser.IdentifierExprContext
import com.developersam.pl.sapl.antlr.PLParser.IfElseExprContext
import com.developersam.pl.sapl.antlr.PLParser.LetExprContext
import com.developersam.pl.sapl.antlr.PLParser.LiteralExprContext
import com.developersam.pl.sapl.antlr.PLParser.MatchExprContext
import com.developersam.pl.sapl.antlr.PLParser.NestedExprContext
import com.developersam.pl.sapl.antlr.PLParser.NotExprContext
import com.developersam.pl.sapl.antlr.PLParser.StringConcatExprContext
import com.developersam.pl.sapl.antlr.PLParser.TermExprContext
import com.developersam.pl.sapl.antlr.PLParser.ThrowExprContext
import com.developersam.pl.sapl.antlr.PLParser.TryCatchFinallyExprContext
import com.developersam.pl.sapl.ast.BinaryExpr
import com.developersam.pl.sapl.ast.BinaryOperator
import com.developersam.pl.sapl.ast.Expression
import com.developersam.pl.sapl.ast.FunctionApplicationExpr
import com.developersam.pl.sapl.ast.FunctionExpr
import com.developersam.pl.sapl.ast.IfElseExpr
import com.developersam.pl.sapl.ast.LetExpr
import com.developersam.pl.sapl.ast.LiteralBuilder
import com.developersam.pl.sapl.ast.LiteralExpr
import com.developersam.pl.sapl.ast.MatchExpr
import com.developersam.pl.sapl.ast.MemberAccessExpr
import com.developersam.pl.sapl.ast.NotExpr
import com.developersam.pl.sapl.ast.ThrowExpr
import com.developersam.pl.sapl.ast.TryCatchFinallyExpr
import com.developersam.pl.sapl.ast.VariableIdentifierExpr
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [ExprBuilder] builds expression AST from parse tree.
 */
internal object ExprBuilder : PLBaseVisitor<Expression>() {

    override fun visitNestedExpr(ctx: NestedExprContext): Expression =
            ctx.expression().accept(this)

    override fun visitLiteralExpr(ctx: LiteralExprContext): Expression =
            LiteralExpr(literal = LiteralBuilder.from(text = ctx.Literal().text))

    override fun visitIdentifierExpr(ctx: IdentifierExprContext): Expression {
        val last = ctx.LowerIdentifier().text
        val prefixes: List<TerminalNode> = ctx.UpperIdentifier()
        return if (prefixes.isEmpty()) {
            VariableIdentifierExpr(variable = last)
        } else{
            MemberAccessExpr(moduleChain = prefixes.map(TerminalNode::getText), member = last)
        }
    }

    override fun visitFunctionApplicationExpr(ctx: FunctionApplicationExprContext): Expression {
        val exprContextList = ctx.expression()
        val functionExpr = exprContextList[0].accept(this)
        val arguments = arrayListOf<Expression>()
        for (i in 1 until exprContextList.size) {
            arguments.add(exprContextList[i].accept(this))
        }
        return FunctionApplicationExpr(functionExpr, arguments)
    }

    override fun visitBitExpr(ctx: BitExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.BitOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitFactorExpr(ctx: FactorExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.FactorOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitTermExpr(ctx: TermExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.TermOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitStringConcatExpr(ctx: StringConcatExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.STR_CONCAT().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitBooleanExpr(ctx: BooleanExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.BinaryLogicalOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitComparisonExpr(ctx: ComparisonExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.ComparisonOperator().text),
                    right = ctx.expression(1).accept(this)
            )

    override fun visitNotExpr(ctx: NotExprContext): Expression =
            NotExpr(expr = ctx.expression().accept(this))

    override fun visitLetExpr(ctx: LetExprContext): Expression =
            LetExpr(
                    identifier = ctx.LowerIdentifier().text,
                    typeAnnotation = ctx.typeAnnotation().accept(TypeExprInAnnotationBuilder),
                    e1 = ctx.expression(0).accept(this),
                    e2 = ctx.expression(1).accept(this)
            )

    override fun visitFunExpr(ctx: FunExprContext): Expression =
            FunctionExpr(
                    arguments = ctx.argumentDeclaration()
                            .map { it.accept(ArgumentDeclarationBuilder) },
                    returnType = ctx.typeAnnotation().typeExprInAnnotation()
                            .accept(TypeExprInAnnotationBuilder),
                    body = ctx.expression().accept(this)
            )

    override fun visitIfElseExpr(ctx: IfElseExprContext): Expression =
            IfElseExpr(
                    condition = ctx.expression(0).accept(this),
                    e1 = ctx.expression(1).accept(this),
                    e2 = ctx.expression(2).accept(this)
            )

    override fun visitMatchExpr(ctx: MatchExprContext): Expression =
            MatchExpr(
                    identifier = ctx.LowerIdentifier().text,
                    matchingList = ctx.patternToExpr().map { c ->
                        val pattern = c.pattern().accept(PatternBuilder)
                        val expr = c.expression().accept(this)
                        pattern to expr
                    }
            )

    override fun visitThrowExpr(ctx: ThrowExprContext): Expression =
            ThrowExpr(ctx.expression().accept(this))

    override fun visitTryCatchFinallyExpr(ctx: TryCatchFinallyExprContext): Expression =
            TryCatchFinallyExpr(
                    tryExpr = ctx.expression(0).accept(this),
                    exception = ctx.LowerIdentifier().text,
                    catchHandler = ctx.expression(1).accept(this),
                    finallyHandler = ctx.expression(2).accept(this)
            )

}
