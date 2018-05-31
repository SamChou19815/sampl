package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.BinaryOperator
import com.developersam.pl.sapl.ast.BinaryOperator.*
import com.developersam.pl.sapl.ast.FunctionTypeInAnnotation
import com.developersam.pl.sapl.ast.Literal
import com.developersam.pl.sapl.ast.SingleIdentifierTypeInAnnotation
import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.VariantTypeInDeclaration
import com.developersam.pl.sapl.ast.boolTypeExpr
import com.developersam.pl.sapl.ast.charTypeExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedBinaryExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression
import com.developersam.pl.sapl.ast.decorated.DecoratedFunctionApplicationExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedFunctionExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedIfElseExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedLetExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedLiteralExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedMatchExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedNotExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedPattern
import com.developersam.pl.sapl.ast.decorated.DecoratedThrowExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedTryCatchExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedVariableIdentifierExpr
import com.developersam.pl.sapl.ast.floatTypeExpr
import com.developersam.pl.sapl.ast.intTypeExpr
import com.developersam.pl.sapl.ast.stringTypeExpr
import com.developersam.pl.sapl.exceptions.GenericInfoWrongNumberOfArgumentsError
import com.developersam.pl.sapl.exceptions.NonExhaustivePatternMatchingError
import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.exceptions.TooManyArgumentsError
import com.developersam.pl.sapl.exceptions.UndefinedIdentifierError
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.exceptions.UnmatchableTypeError
import com.developersam.pl.sapl.exceptions.UnusedPatternError
import com.developersam.pl.sapl.environment.TypeCheckingEnv
import com.developersam.pl.sapl.util.toFunctionTypeExpr

/**
 * [Expression] represents a set of supported expression.
 */
internal sealed class Expression {

    /**
     * [typeCheck] returns the decorated expression with the inferred type  under the given
     * [environment].
     *
     * If the type checking failed, it should throw [UnexpectedTypeError] to indicate what's wrong.
     */
    abstract fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression

}

/**
 * [LiteralExpr] represents a [literal] as an expression.
 */
internal data class LiteralExpr(val literal: Literal) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            DecoratedLiteralExpr(literal = literal, type = literal.inferredType)

}

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression.
 * It can only contain [genericInfo] which helps to determine the fixed type for this expression.
 */
internal data class VariableIdentifierExpr(
        val variable: String, private val genericInfo: List<TypeExprInAnnotation>
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val typeInfo = environment[variable]
                ?: throw UndefinedIdentifierError(badIdentifier = variable)
        val genericSymbolsToSubstitute = typeInfo.genericInfo
        if (genericSymbolsToSubstitute.size != genericInfo.size) {
            throw GenericInfoWrongNumberOfArgumentsError(
                    expectedNumber = genericSymbolsToSubstitute.size,
                    actualNumber = genericInfo.size
            )
        }
        val substitutionMap = genericSymbolsToSubstitute.zip(genericInfo).toMap()
        val type = typeInfo.typeExpr.substituteGenericInfo(map = substitutionMap)
        return DecoratedVariableIdentifierExpr(
                variable = variable, genericInfo = genericInfo, type = type
        )
    }

}

/**
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function.
 */
internal data class FunctionApplicationExpr(
        val functionExpr: Expression, val arguments: List<Expression>
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        // TODO fix generic function problem
        val decoratedFunctionExpr = functionExpr.typeCheck(environment = environment)
        val functionType = decoratedFunctionExpr.type
        when (functionType) {
            is SingleIdentifierTypeInAnnotation -> throw UnexpectedTypeError(
                    expectedType = "<function>", actualType = functionType
            )
            is FunctionTypeInAnnotation -> {
                var expectedArg: TypeExprInAnnotation? = functionType.argumentType
                var returnType = functionType.returnType
                val decoratedArgumentExpr = arrayListOf<DecoratedExpression>()
                for (expr in arguments) {
                    val expType = expectedArg ?: throw TooManyArgumentsError()
                    val decoratedExpr = expr.typeCheck(environment = environment)
                    decoratedArgumentExpr.add(element = decoratedExpr)
                    val exprType = decoratedExpr.type
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
                return DecoratedFunctionApplicationExpr(
                        functionExpr = decoratedFunctionExpr, arguments = decoratedArgumentExpr,
                        type = returnType
                )
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

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            when (op) {
                SHL, SHR, USHR, XOR, LAND, LOR, MUL, DIV, MOD, PLUS, MINUS -> {
                    // int binary operators
                    val leftExpr = left.typeCheck(environment = environment)
                    val leftType = leftExpr.type
                    if (leftType != intTypeExpr) {
                        throw UnexpectedTypeError(
                                expectedType = intTypeExpr, actualType = leftType
                        )
                    }
                    val rightExpr = right.typeCheck(environment = environment)
                    val rightType = rightExpr.type
                    if (rightType == intTypeExpr) {
                        DecoratedBinaryExpr(left = leftExpr, op = op,
                                right = rightExpr, type = intTypeExpr)
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = intTypeExpr, actualType = rightType
                        )
                    }
                }
                F_MUL, F_DIV, F_PLUS, F_MINUS -> {
                    // float binary operators
                    val leftExpr = left.typeCheck(environment = environment)
                    val leftType = leftExpr.type
                    if (leftType != floatTypeExpr) {
                        throw UnexpectedTypeError(
                                expectedType = floatTypeExpr, actualType = leftType
                        )
                    }
                    val rightExpr = right.typeCheck(environment = environment)
                    val rightType = rightExpr.type
                    if (rightType == floatTypeExpr) {
                        DecoratedBinaryExpr(left = leftExpr, op = op,
                                right = rightExpr, type = floatTypeExpr)
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = floatTypeExpr, actualType = rightType
                        )
                    }
                }
                LT, LE, GT, GE -> {
                    // comparison type operator
                    val leftExpr = left.typeCheck(environment = environment)
                    val leftType = leftExpr.type
                    when (leftType) {
                        intTypeExpr, floatTypeExpr, charTypeExpr, stringTypeExpr -> {
                            val rightExpr = right.typeCheck(environment = environment)
                            val rightType = rightExpr.type
                            if (leftType == rightType) {
                                DecoratedBinaryExpr(left = leftExpr, op = op,
                                        right = rightExpr, type = boolTypeExpr)
                            } else {
                                throw UnexpectedTypeError(
                                        expectedType = leftType, actualType = rightType
                                )
                            }
                        }
                        else -> throw UnexpectedTypeError(
                                expectedType = intTypeExpr, actualType = leftType
                        )
                    }
                }
                REF_EQ, STRUCT_EQ, REF_NE, STRUCT_NE -> {
                    // equality operator
                    val leftExpr = left.typeCheck(environment = environment)
                    val leftType = leftExpr.type
                    val rightExpr = right.typeCheck(environment = environment)
                    val rightType = rightExpr.type
                    if (leftType == rightType) {
                        DecoratedBinaryExpr(left = leftExpr, op = op,
                                right = rightExpr, type = boolTypeExpr)
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = leftType, actualType = rightType
                        )
                    }
                }
            }

}

/**
 * [NotExpr] represents the logical inversion of expression [expr].
 */
internal data class NotExpr(val expr: Expression) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val e = expr.typeCheck(environment = environment)
        val t = e.type
        if (t == boolTypeExpr) {
            return DecoratedNotExpr(expr = e, type = boolTypeExpr)
        } else {
            throw UnexpectedTypeError(expectedType = boolTypeExpr, actualType = t)
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

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            if (environment[identifier] == null) {
                val decoratedE1 = e1.typeCheck(environment = environment)
                val e1TypeInfo = decoratedE1.type.asTypeInformation
                val newEnv = environment.put(variable = identifier, typeInfo = e1TypeInfo)
                val decoratedE2 = e2.typeCheck(environment = newEnv)
                val e2Type = decoratedE2.type
                DecoratedLetExpr(
                        identifier = identifier, e1 = decoratedE1, e2 = decoratedE2, type = e2Type
                )
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

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val functionDeclaredType = toFunctionTypeExpr(
                argumentTypes = arguments.map { it.second }, returnType = returnType
        )
        val bodyExpr = body.typeCheck(environment = environment)
        val bodyType = bodyExpr.type
        if (returnType != bodyType) {
            throw UnexpectedTypeError(expectedType = returnType, actualType = bodyType)
        }
        return DecoratedFunctionExpr(
                arguments = arguments, returnType = returnType, body = bodyExpr,
                type = functionDeclaredType
        )
    }

}

/**
 * [IfElseExpr] represents the if else expression, guarded by [condition] and having two
 * branches [e1] and [e2].
 */
internal data class IfElseExpr(
        val condition: Expression, val e1: Expression, val e2: Expression
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val conditionExpr = condition.typeCheck(environment = environment)
        val conditionType = conditionExpr.type
        if (conditionType != boolTypeExpr) {
            throw UnexpectedTypeError(expectedType = boolTypeExpr, actualType = conditionType)
        }
        val decoratedE1 = e1.typeCheck(environment = environment)
        val t1 = decoratedE1.type
        val decoratedE2 = e2.typeCheck(environment = environment)
        val t2 = decoratedE2.type
        if (t1 != t2) {
            throw UnexpectedTypeError(expectedType = t1, actualType = t2)
        }
        return DecoratedIfElseExpr(
                condition = conditionExpr, e1 = decoratedE1, e2 = decoratedE2, type = t1
        )
    }

}

/**
 * [MatchExpr] represents the pattern matching expression, with a list of [matchingList] to match
 * against [exprToMatch].
 */
internal data class MatchExpr(
        val exprToMatch: Expression, val matchingList: List<Pair<Pattern, Expression>>
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedExprToMatch = exprToMatch.typeCheck(environment = environment)
        val typeToMatch = decoratedExprToMatch.type
        val typeIdentifier = (typeToMatch as? SingleIdentifierTypeInAnnotation)?.identifier
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        val typeDefinitionOpt = environment.typeDefinitions[typeIdentifier]
                as? VariantTypeInDeclaration
        val variantTypeDeclarations = typeDefinitionOpt?.map?.toMutableMap()
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        var type: TypeExprInAnnotation? = null
        val decoratedMatchingList = arrayListOf<Pair<DecoratedPattern, DecoratedExpression>>()
        for ((pattern, expr) in matchingList) {
            if (variantTypeDeclarations.isEmpty()) {
                throw UnusedPatternError(pattern = pattern)
            }
            val (decoratedPattern, newEnv) = pattern.typeCheck(
                    typeToMatch = typeToMatch, environment = environment,
                    variantTypeDefs = variantTypeDeclarations
            )
            val decoratedExpr = expr.typeCheck(environment = newEnv)
            decoratedMatchingList.add(decoratedPattern to decoratedExpr)
            val exprType = decoratedExpr.type
            val knownType = type
            if (knownType == null) {
                type = exprType
            } else {
                if (knownType != exprType) {
                    throw UnexpectedTypeError(expectedType = knownType, actualType = exprType)
                }
            }
        }
        if (variantTypeDeclarations.isNotEmpty()) {
            throw NonExhaustivePatternMatchingError()
        }
        return DecoratedMatchExpr(
                exprToMatch = decoratedExprToMatch, matchingList = decoratedMatchingList,
                type = type ?: throw NonExhaustivePatternMatchingError()
        )
    }

}

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 * The throw expression is coerced to have [type].
 */
internal data class ThrowExpr(val type: TypeExprInAnnotation, val expr: Expression) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val e = expr.typeCheck(environment = environment)
        val t = e.type
        if (t != stringTypeExpr) {
            throw UnexpectedTypeError(expectedType = stringTypeExpr, actualType = t)
        }
        return DecoratedThrowExpr(type = type, expr = e)
    }

}

/**
 * [TryCatchExpr] represents the try catch finally structure as an expression, where the
 * [tryExpr] is evaluated, and guard by catch branch with [exception] in scope and [catchHandler]
 * to deal with it.
 */
internal data class TryCatchExpr(
        val tryExpr: Expression, val exception: String, val catchHandler: Expression
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedTryExpr = tryExpr.typeCheck(environment = environment)
        val tryType = decoratedTryExpr.type
        val decoratedCatchExpr = catchHandler.typeCheck(environment = environment.put(
                variable = exception, typeInfo = stringTypeExpr.asTypeInformation
        ))
        val catchType = decoratedCatchExpr.type
        if (tryType != catchType) {
            throw UnexpectedTypeError(expectedType = tryType, actualType = catchType)
        }
        return DecoratedTryCatchExpr(
                tryExpr = decoratedTryExpr, exception = exception,
                catchHandler = decoratedCatchExpr, type = tryType
        )
    }

}
