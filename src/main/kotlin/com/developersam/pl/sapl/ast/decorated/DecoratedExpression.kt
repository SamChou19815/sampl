package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.BinaryOperator
import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.raw.Expression
import com.developersam.pl.sapl.ast.raw.Literal
import com.developersam.pl.sapl.ast.raw.MatchExpr
import com.developersam.pl.sapl.ast.raw.Pattern
import com.developersam.pl.sapl.ast.raw.ThrowExpr
import com.developersam.pl.sapl.ast.raw.TryCatchExpr

/**
 * [DecoratedExpression] is an expression with a correct decorated type.
 */
internal sealed class DecoratedExpression {
    /**
     * [type] is the type decoration.
     */
    abstract val type: TypeExprInAnnotation
}

/**
 * [DecoratedLiteralExpr] with correct [type] represents a [literal] as an expression.
 */
internal data class DecoratedLiteralExpr(
        val literal: Literal, override val type: TypeExprInAnnotation
) : DecoratedExpression()


/**
 * [DecoratedVariableIdentifierExpr] with correct [type] represents a [variable] identifier as an
 * expression.
 * It can only contain [genericInfo] which helps to determine the fixed type for this expression.
 */
internal data class DecoratedVariableIdentifierExpr(
        val variable: String, val genericInfo: List<TypeExprInAnnotation>,
        override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedFunctionApplicationExpr] with correct [type] is the function application expression,
 * with [functionExpr] as the function and [arguments] as arguments of the function.
 */
internal data class DecoratedFunctionApplicationExpr(
        val functionExpr: DecoratedExpression, val arguments: List<DecoratedExpression>,
        override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedBinaryExpr] with correct [type] represents a binary expression with operator [op]
 * between [left] and [right].
 */
internal data class DecoratedBinaryExpr(
        val left: DecoratedExpression, val op: BinaryOperator, val right: DecoratedExpression,
        override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedNotExpr] with correct [type] represents the logical inversion of expression [expr].
 */
internal data class DecoratedNotExpr(
        val expr: DecoratedExpression, override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedLetExpr] with correct [type] represents the let expression of the form
 * `let` [identifier] `=` [e1] `;` [e2]
 */
internal data class DecoratedLetExpr(
        val identifier: String, val e1: DecoratedExpression, val e2: DecoratedExpression,
        override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedFunctionExpr] with correct [type] is the function expression with some [arguments],
 * a [returnType] and finally the function [body].
 */
internal data class DecoratedFunctionExpr(
        val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: DecoratedExpression,
        override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedIfElseExpr] with correct [type] represents the if else expression, guarded by
 * [condition] and having two branches [e1] and [e2].
 */
internal data class DecoratedIfElseExpr(
        val condition: DecoratedExpression, val e1: DecoratedExpression,
        val e2: DecoratedExpression, override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedMatchExpr] with correct [type] represents the pattern matching expression, with a list
 * of [matchingList] to match against [exprToMatch].
 */
internal data class DecoratedMatchExpr(
        val exprToMatch: DecoratedExpression,
        val matchingList: List<Pair<DecoratedPattern, DecoratedExpression>>,
        override val type: TypeExprInAnnotation
) : DecoratedExpression()

/**
 * [DecoratedThrowExpr] with correct [type] represents the throw exception expression, where the
 * thrown exception is [expr]. The throw expression is coerced to have [type].
 */
internal data class DecoratedThrowExpr(
        override val type: TypeExprInAnnotation, val expr: DecoratedExpression
) : DecoratedExpression()

/**
 * [DecoratedTryCatchExpr] with correct [type] represents the try catch finally structure as an
 * expression, where the [tryExpr] is evaluated, and guard by catch branch with [exception] in scope
 * and [catchHandler] to deal with it.
 */
internal data class DecoratedTryCatchExpr(
        val tryExpr: DecoratedExpression, val exception: String,
        val catchHandler: DecoratedExpression, override val type: TypeExprInAnnotation
) : DecoratedExpression()
