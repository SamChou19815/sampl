package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.LanguageBaseVisitor
import com.developersam.pl.sapl.antlr.LanguageParser.*
import com.developersam.pl.sapl.ast.BinaryExpr
import com.developersam.pl.sapl.ast.BinaryOperator
import com.developersam.pl.sapl.ast.Expression
import com.developersam.pl.sapl.ast.FunctionApplicationExpr
import com.developersam.pl.sapl.ast.IfElseExpr
import com.developersam.pl.sapl.ast.LetExpr
import com.developersam.pl.sapl.ast.LiteralBuilder
import com.developersam.pl.sapl.ast.LiteralExpr
import com.developersam.pl.sapl.ast.MatchExpr
import com.developersam.pl.sapl.ast.NotExpr
import com.developersam.pl.sapl.ast.ThrowExpr
import com.developersam.pl.sapl.ast.TryCatchFinallyExpr
import com.developersam.pl.sapl.ast.VariableIdentifierExpr
import com.developersam.pl.sapl.util.symbolicName

/**
 * [ExprBuilder] builds expression AST from parse tree.
 */
object ExprBuilder : LanguageBaseVisitor<Expression>() {

    override fun visitNestedExpr(ctx: NestedExprContext): Expression =
            ctx.expression().accept(this)

    override fun visitLiteralExpr(ctx: LiteralExprContext): Expression =
            LiteralExpr(literal = LiteralBuilder.from(text = ctx.Literal().text))

    override fun visitIdentifierExpr(ctx: IdentifierExprContext): Expression =
            VariableIdentifierExpr(variable = ctx.LowerIdentifier().text)

    override fun visitIdentifierInModuleExpr(ctx: IdentifierInModuleExprContext): Expression {
        TODO(reason = "Module not setup yet.")
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

    override fun visitBitExpr(ctx: BitExprContext): Expression {
        val left = ctx.expression(0).accept(this)
        val op = BinaryOperator.valueOf(value = ctx.BitOperator().symbolicName)
        val right = ctx.expression(1).accept(this)
        return BinaryExpr(left, op, right)
    }

    override fun visitFactorExpr(ctx: FactorExprContext): Expression {
        val left = ctx.expression(0).accept(this)
        val op = BinaryOperator.valueOf(value = ctx.FactorOperator().symbolicName)
        val right = ctx.expression(1).accept(this)
        return BinaryExpr(left, op, right)
    }

    override fun visitTermExpr(ctx: TermExprContext): Expression {
        val left = ctx.expression(0).accept(this)
        val op = BinaryOperator.valueOf(value = ctx.TermOperator().symbolicName)
        val right = ctx.expression(1).accept(this)
        return BinaryExpr(left, op, right)
    }

    override fun visitStringConcatExpr(ctx: StringConcatExprContext): Expression {
        val left = ctx.expression(0).accept(this)
        val op = BinaryOperator.valueOf(value = ctx.STR_CONCAT().symbolicName)
        val right = ctx.expression(1).accept(this)
        return BinaryExpr(left, op, right)
    }

    override fun visitBooleanExpr(ctx: BooleanExprContext): Expression {
        val left = ctx.expression(0).accept(this)
        val op = BinaryOperator.valueOf(value = ctx.BinaryLogicalOperator().symbolicName)
        val right = ctx.expression(1).accept(this)
        return BinaryExpr(left, op, right)
    }

    override fun visitComparisonExpr(ctx: ComparisonExprContext): Expression {
        val left = ctx.expression(0).accept(this)
        val op = BinaryOperator.valueOf(value = ctx.ComparisonOperator().symbolicName)
        val right = ctx.expression(1).accept(this)
        return BinaryExpr(left, op, right)
    }

    override fun visitNotExpr(ctx: NotExprContext): Expression =
            NotExpr(expr = ctx.expression().accept(this))

    override fun visitLetExpr(ctx: LetExprContext): Expression {
        val pattern = ctx.pattern().accept(PatternBuilder)
        val e1 = ctx.expression(0).accept(this)
        val e2 = ctx.expression(1).accept(this)
        return LetExpr(pattern, e1, e2)
    }

    override fun visitLambdaExpr(ctx: LambdaExprContext): Expression {
        TODO(reason = "Lambda not setup yet.")
    }

    override fun visitIfElseExpr(ctx: IfElseExprContext): Expression {
        val condition = ctx.expression(0).accept(this)
        val e1 = ctx.expression(1).accept(this)
        val e2 = ctx.expression(2).accept(this)
        return IfElseExpr(condition, e1, e2)
    }

    override fun visitMatchExpr(ctx: MatchExprContext): Expression {
        val id = ctx.LowerIdentifier().text
        val matching = ctx.patternToExpr().map { c ->
            val pattern = c.pattern().accept(PatternBuilder)
            val expr = c.expression().accept(this)
            pattern to expr
        }
        return MatchExpr(identifier = id, matchingList = matching)
    }

    override fun visitThrowExpr(ctx: ThrowExprContext): Expression =
            ThrowExpr(ctx.expression().accept(this))

    override fun visitTryCatchFinallyExpr(ctx: TryCatchFinallyExprContext): Expression {
        val tryExpr = ctx.expression(0).accept(this)
        val exception = ctx.LowerIdentifier().text
        val catchExpr = ctx.expression(1).accept(this)
        val finallyExpr = ctx.expression(2).accept(this)
        return TryCatchFinallyExpr(
                tryExpr = tryExpr, exception = exception,
                catchHandler = catchExpr, finallyHandler = finallyExpr
        )
    }

}