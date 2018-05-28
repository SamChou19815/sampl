package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.typecheck.TypeCheckerEnvironment

/**
 * [Expression] represents a set of supported expression.
 */
internal sealed class Expression : AstNode {

    /**
     * [inferType] returns the inferred type from the expression under the given [environment].
     *
     * If the type checking failed, it should throw [UnexpectedTypeError] to indicate what's wrong.
     */
    abstract fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier

    final override fun <T> accept(visitor: AstVisitor<T>): T = visitor.visit(expression = this)

}

/**
 * [LiteralExpr] represents a [literal] as an expression.
 */
internal data class LiteralExpr(val literal: Literal) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier =
            literal.inferredType

}

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression.
 */
internal data class VariableIdentifierExpr(val variable: String) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [MemberAccessExpr] represents the member access from modules with module [moduleChain]'s member
 * [member].
 */
internal data class MemberAccessExpr(
        val moduleChain: List<String>, val member: String
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function.
 */
internal data class FunctionApplicationExpr(
        val functionExpr: Expression, val arguments: List<Expression>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [BinaryExpr] represents a binary expression with operator [op] between [left] and [right].
 */
internal data class BinaryExpr(
        val left: Expression, val op: BinaryOperator, val right: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [NotExpr] represents the logical inversion of expression. [expr].
 */
internal data class NotExpr(val expr: Expression) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [LetExpr] represents the let expression of the form
 * `let` [identifier] (: [typeAnnotation]) `=` [e1] `;` [e2]
 */
internal data class LetExpr(
        val identifier: String, val typeAnnotation: TypeExprInAnnotation?,
        val e1: Expression, val e2: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [FunctionExpr] is the function expression with some [genericsDeclaration], some [arguments] and
 * a [returnType] and finally the function [body].
 */
internal data class FunctionExpr(
        val genericsDeclaration: Set<String>, val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [IfElseExpr] represents the if else expression, guarded by [condition] and having two
 * branches [e1] and [e2].
 */
internal data class IfElseExpr(
        val condition: Expression, val e1: Expression, val e2: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [MatchExpr] represents the pattern matching expression, with a list of [matchingList] to match
 * against [identifier].
 */
internal data class MatchExpr(
        val identifier: String, val matchingList: List<Pair<Pattern, Expression>>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 */
internal data class ThrowExpr(val expr: Expression) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}

/**
 * [TryCatchFinallyExpr] represents the try catch finally structure as an expression, where the
 * [tryExpr] is evaluated, and guard by catch branch with [exception] in scope and [catchHandler]
 * to deal with it. It has an optional [finallyHandler] branch.
 */
internal data class TryCatchFinallyExpr(
        val tryExpr: Expression, val exception: String,
        val catchHandler: Expression, val finallyHandler: Expression? = null
) : Expression() {

    override fun inferType(environment: TypeCheckerEnvironment): TypeIdentifier {
        TODO()
    }

}
