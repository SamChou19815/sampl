package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.BitExprContext
import com.developersam.pl.sapl.antlr.PLParser.ComparisonExprContext
import com.developersam.pl.sapl.antlr.PLParser.ConjunctionExprContext
import com.developersam.pl.sapl.antlr.PLParser.DisjunctionExprContext
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
import com.developersam.pl.sapl.antlr.PLParser.TermExprContext
import com.developersam.pl.sapl.antlr.PLParser.ThrowExprContext
import com.developersam.pl.sapl.antlr.PLParser.TryCatchExprContext
import com.developersam.pl.sapl.ast.BinaryOperator
import com.developersam.pl.sapl.ast.Literal
import com.developersam.pl.sapl.ast.raw.BinaryExpr
import com.developersam.pl.sapl.ast.raw.Expression
import com.developersam.pl.sapl.ast.raw.FunctionApplicationExpr
import com.developersam.pl.sapl.ast.raw.FunctionExpr
import com.developersam.pl.sapl.ast.raw.IfElseExpr
import com.developersam.pl.sapl.ast.raw.LetExpr
import com.developersam.pl.sapl.ast.raw.LiteralExpr
import com.developersam.pl.sapl.ast.raw.MatchExpr
import com.developersam.pl.sapl.ast.raw.NotExpr
import com.developersam.pl.sapl.ast.raw.ThrowExpr
import com.developersam.pl.sapl.ast.raw.TryCatchExpr
import com.developersam.pl.sapl.ast.raw.VariableIdentifierExpr
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [ExprBuilder] builds expression AST from parse tree.
 */
internal object ExprBuilder : PLBaseVisitor<Expression>() {

    override fun visitNestedExpr(ctx: NestedExprContext): Expression =
            ctx.expression().accept(this)

    override fun visitLiteralExpr(ctx: LiteralExprContext): Expression =
            LiteralExpr(literal = Literal.from(text = ctx.Literal().text))

    override fun visitIdentifierExpr(ctx: IdentifierExprContext): Expression =
            VariableIdentifierExpr(
                    variable = ctx.UpperIdentifier()
                            .joinToString(separator = ".", transform = TerminalNode::getText)
                            + "." + ctx.LowerIdentifier().text,
                    genericInfo = ctx.genericsSpecialization()
                            ?.typeExprInAnnotation()
                            ?.map { it.accept(TypeExprInAnnotationBuilder) }
                            ?: emptyList()
            )

    override fun visitNotExpr(ctx: NotExprContext): Expression =
            NotExpr(expr = ctx.expression().accept(this))

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

    override fun visitComparisonExpr(ctx: ComparisonExprContext): Expression =
            BinaryExpr(
                    left = ctx.expression(0).accept(this),
                    op = BinaryOperator.fromRaw(text = ctx.ComparisonOperator().text),
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

    override fun visitLetExpr(ctx: LetExprContext): Expression =
            LetExpr(
                    identifier = ctx.LowerIdentifier().text,
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

}
