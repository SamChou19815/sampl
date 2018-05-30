package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.ast.BinaryOperator.*
import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.exceptions.TooManyArgumentsError
import com.developersam.pl.sapl.exceptions.UndefinedIdentifierError
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.typecheck.TypeCheckerEnv
import com.developersam.pl.sapl.util.toFunctionTypeExpr

/**
 * [Expression] represents a set of supported expression.
 */
internal sealed class Expression {

    /**
     * [inferType] returns the inferred type from the expression under the given [environment].
     *
     * If the type checking failed, it should throw [UnexpectedTypeError] to indicate what's wrong.
     */
    abstract fun inferType(environment: TypeCheckerEnv): TypeInformation

}

/**
 * [LiteralExpr] represents a [literal] as an expression.
 */
internal data class LiteralExpr(val literal: Literal) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation =
            literal.inferredType

}

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression.
 */
internal data class VariableIdentifierExpr(val variable: String) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation =
            environment.getTypeInfo(variable = variable)
                    ?: throw UndefinedIdentifierError(badIdentifier = variable)

}

/**
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function.
 */
internal data class FunctionApplicationExpr(
        val functionExpr: Expression, val arguments: List<Expression>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        val functionType = functionExpr.inferType(environment = environment)
        val typeExpr = functionType.typeExpr
        return when (typeExpr) {
            is SingleIdentifierTypeInAnnotation -> throw UnexpectedTypeError(
                    expectedType = "<function>", actualType = functionType
            )
            is FunctionTypeInAnnotation -> {
                var expectedArg: TypeExprInAnnotation? = typeExpr.argumentType
                var returnType = typeExpr.returnType
                for (expr in arguments) {
                    val expType = TypeInformation(
                            typeExpr = expectedArg ?: throw TooManyArgumentsError(),
                            genericInfo = functionType.genericInfo
                    )
                    val exprType = expr.inferType(environment = environment)
                    if (expType != exprType) {
                        throw UnexpectedTypeError(expectedType = expType, actualType = exprType)
                    }
                    val r = returnType
                    when (r) {
                        is SingleIdentifierTypeInAnnotation -> {
                            expectedArg = null
                        }
                        is FunctionTypeInAnnotation -> {
                            expectedArg = r.argumentType
                            returnType = r.returnType
                        }
                    }
                }
                TypeInformation(typeExpr = returnType, genericInfo = functionType.genericInfo)
            }
        }
    }

}

/**
 * [BinaryExpr] represents a binary expression with operator [op] between [left] and [right].
 */
internal data class BinaryExpr(
        val left: Expression, val op: BinaryOperator, val right: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation =
            when (op) {
                SHL, SHR, USHR, XOR, LAND, LOR, MUL, DIV, MOD, PLUS, MINUS -> {
                    // int binary operators
                    val leftType = left.inferType(environment = environment)
                    if (leftType != intTypeInfo) {
                        throw UnexpectedTypeError(
                                expectedType = intTypeInfo, actualType = leftType
                        )
                    }
                    val rightType = right.inferType(environment = environment)
                    if (rightType == intTypeInfo) {
                        intTypeInfo
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = intTypeInfo, actualType = rightType
                        )
                    }
                }
                F_MUL, F_DIV, F_PLUS, F_MINUS -> {
                    // float binary operators
                    val leftType = left.inferType(environment = environment)
                    if (leftType != floatTypeInfo) {
                        throw UnexpectedTypeError(
                                expectedType = floatTypeInfo, actualType = leftType
                        )
                    }
                    val rightType = right.inferType(environment = environment)
                    if (rightType == floatTypeInfo) {
                        floatTypeInfo
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = floatTypeInfo, actualType = rightType
                        )
                    }
                }
                LT, LE, GT, GE -> {
                    // comparison type operator
                    val leftType = left.inferType(environment = environment)
                    when (leftType) {
                        intTypeInfo, floatTypeInfo, charTypeInfo, stringTypeInfo -> {
                            val rightType = right.inferType(environment = environment)
                            if (leftType == rightType) {
                                boolTypeInfo
                            } else {
                                throw UnexpectedTypeError(
                                        expectedType = leftType, actualType = rightType
                                )
                            }
                        }
                        else -> throw UnexpectedTypeError(
                                expectedType = intTypeInfo, actualType = leftType
                        )
                    }
                }
                REF_EQ, STRUCT_EQ, REF_NE, STRUCT_NE -> {
                    // equality operator
                    val leftType = left.inferType(environment = environment)
                    val rightType = right.inferType(environment = environment)
                    if (leftType == rightType) {
                        boolTypeInfo
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = leftType, actualType = rightType
                        )
                    }
                }
            }

}

/**
 * [NotExpr] represents the logical inversion of expression. [expr].
 */
internal data class NotExpr(val expr: Expression) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        val t = expr.inferType(environment = environment)
        if (t == boolTypeInfo) {
            return boolTypeInfo
        } else {
            throw UnexpectedTypeError(expectedType = boolTypeInfo, actualType = t)
        }
    }

}

/**
 * [LetExpr] represents the let expression of the form
 * `let` [identifier] `=` [e1] `;` [e2]
 */
internal data class LetExpr(
        val identifier: String, val e1: Expression, val e2: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation =
            if (environment.getTypeInfo(variable = identifier) == null) {
                e2.inferType(environment = environment.updateTypeInfo(
                        variable = identifier, typeInfo = e1.inferType(environment = environment)
                ))
            } else {
                throw ShadowedNameError(shadowedName = identifier)
            }

}

/**
 * [FunctionExpr] is the function expression with some [arguments], a [returnType] and finally the
 * function [body].
 */
internal data class FunctionExpr(
        val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        val functionDeclaredType = toFunctionTypeExpr(
                argumentTypes = arguments.map { it.second }, returnType = returnType
        )
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

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        val conditionType = condition.inferType(environment = environment)
        if (conditionType != boolTypeInfo) {
            throw UnexpectedTypeError(expectedType = boolTypeInfo, actualType = conditionType)
        }
        val t1 = e1.inferType(environment = environment)
        val t2 = e2.inferType(environment = environment)
        if (t1 != t2) {
            throw UnexpectedTypeError(expectedType = t1, actualType = t2)
        }
        return t1
    }

}

/**
 * [MatchExpr] represents the pattern matching expression, with a list of [matchingList] to match
 * against [identifier].
 */
internal data class MatchExpr(
        val identifier: String, val matchingList: List<Pair<Pattern, Expression>>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        TODO()
    }

}

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 */
internal data class ThrowExpr(val expr: Expression) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        val t = expr.inferType(environment = environment)
        if (t != stringTypeInfo) {
            throw UnexpectedTypeError(expectedType = stringTypeInfo, actualType = t)
        }
        return TypeInformation(
                typeExpr = SingleIdentifierTypeInAnnotation(
                        identifier = TypeIdentifier(type = "_generic1")
                ),
                genericInfo = setOf(element = "_generic1")
        )
    }

}

/**
 * [TryCatchFinallyExpr] represents the try catch finally structure as an expression, where the
 * [tryExpr] is evaluated, and guard by catch branch with [exception] in scope and [catchHandler]
 * to deal with it.
 */
internal data class TryCatchFinallyExpr(
        val tryExpr: Expression, val exception: String, val catchHandler: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeInformation {
        val tryType = tryExpr.inferType(environment = environment)
        val catchType = catchHandler.inferType(environment = environment.updateTypeInfo(
                variable = exception, typeInfo = stringTypeInfo
        ))
        if (tryType != catchType) {
            throw UnexpectedTypeError(expectedType = tryType, actualType = catchType)
        }
        return tryType
    }

}
