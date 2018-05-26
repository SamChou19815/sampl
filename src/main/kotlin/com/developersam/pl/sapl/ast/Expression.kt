package com.developersam.pl.sapl.ast

/**
 * [Expression] represents a set of supported expression.
 */
sealed class Expression : AstNode

/**
 * [LiteralExpr] represents a [literal] as an expression.
 */
data class LiteralExpr(val literal: Literal) : Expression()

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression.
 */
data class VariableIdentifierExpr(val variable: String) : Expression()

/**
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function.
 */
data class FunctionApplicationExpr(
        val functionExpr: Expression, val arguments: List<Expression>
) : Expression()

/**
 * [BinaryExpr] represents a binary expression with operator [op] between [left] and [right].
 */
data class BinaryExpr(
        val left: Expression, val op: BinaryOperator, val right: Expression
) : Expression()

/**
 * [NotExpr] represents the logical inversion of expression. [expr].
 */
data class NotExpr(val expr: Expression) : Expression()

/**
 * [LetExpr] represents the let expression of the form
 * `let` [identifier] (: [typeAnnotation]) `=` [e1] `;` [e2]
 */
data class LetExpr(
        val identifier: String, val typeAnnotation: TypeExprInAnnotation?,
        val e1: Expression, val e2: Expression
) : Expression()

/**
 * [FunctionExpr] is the function expression with some [genericsDeclaration], some [arguments] and
 * a [returnType] and finally the function [body].
 */
data class FunctionExpr(
    val genericsDeclaration: Set<String>, val arguments: List<Pair<String, String>>,
    val returnType: String, val body: Expression
) : Expression()

/**
 * [IfElseExpr] represents the if else expression, guarded by [condition] and having two
 * branches [e1] and [e2].
 */
data class IfElseExpr(
        val condition: Expression, val e1: Expression, val e2: Expression
) : Expression()

/**
 * [MatchExpr] represents the pattern matching expression, with a list of [matchingList] to match
 * against [identifier].
 */
data class MatchExpr(
        val identifier: String, val matchingList: List<Pair<Pattern, Expression>>
) : Expression()

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 */
data class ThrowExpr(val expr: Expression) : Expression()

/**
 * [TryCatchFinallyExpr] represents the try catch finally structure as an expression, where the
 * [tryExpr] is evaluated, and guard by catch branch with [exception] in scope and [catchHandler]
 * to deal with it. It has an optional [finallyHandler] branch.
 */
data class TryCatchFinallyExpr(
        val tryExpr: Expression, val exception: String,
        val catchHandler: Expression, val finallyHandler: Expression? = null
) : Expression()
