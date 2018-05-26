package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.*
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
import com.developersam.pl.sapl.ast.NotExpr
import com.developersam.pl.sapl.ast.ThrowExpr
import com.developersam.pl.sapl.ast.TryCatchFinallyExpr
import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.UnitNode
import com.developersam.pl.sapl.ast.VariableIdentifierExpr
import org.antlr.v4.runtime.tree.TerminalNode
import java.util.stream.Collectors

/**
 * [ExprBuilder] builds expression AST from parse tree.
 */
object ExprBuilder : PLBaseVisitor<Expression>() {

    override fun visitNestedExpr(ctx: NestedExprContext): Expression =
            ctx.expression().accept(this)

    override fun visitLiteralExpr(ctx: LiteralExprContext): Expression =
            LiteralExpr(literal = LiteralBuilder.from(text = ctx.Literal().text))

    override fun visitIdentifierExpr(ctx: IdentifierExprContext): Expression {
        var variable = ctx.LowerIdentifier().text
        val prefixes: List<TerminalNode> = ctx.UpperIdentifier()
        if (prefixes.isNotEmpty()) {
            variable = prefixes.joinToString(separator = ".") { it.text } + "." + variable
        }
        return VariableIdentifierExpr(variable = variable)
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
                    genericsDeclaration = ctx.genericsDeclaration()
                            ?.accept(GenericsDeclarationBuilder) ?: emptySet(),
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