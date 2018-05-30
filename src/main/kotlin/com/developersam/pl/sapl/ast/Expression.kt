package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.ast.BinaryOperator.*
import com.developersam.pl.sapl.exceptions.GenericInfoWrongNumberOfArgumentsError
import com.developersam.pl.sapl.exceptions.NonExhaustivePatternMatchingError
import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.exceptions.TooManyArgumentsError
import com.developersam.pl.sapl.exceptions.UndefinedIdentifierError
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.exceptions.UnmatchableTypeError
import com.developersam.pl.sapl.exceptions.UnusedPatternError
import com.developersam.pl.sapl.exceptions.WrongPatternError
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
    abstract fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation

}

/**
 * [LiteralExpr] represents a [literal] as an expression.
 */
internal data class LiteralExpr(val literal: Literal) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation =
            literal.inferredType

}

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression.
 * It can only contain [genericInfo] which helps to determine the fixed type for this expression.
 */
internal data class VariableIdentifierExpr(
        val variable: String, private val genericInfo: List<TypeExprInAnnotation>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
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
        return typeInfo.typeExpr.substituteGenericInfo(map = substitutionMap)
    }

}

/**
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function.
 */
internal data class FunctionApplicationExpr(
        val functionExpr: Expression, val arguments: List<Expression>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val functionType = functionExpr.inferType(environment = environment)
        return when (functionType) {
            is SingleIdentifierTypeInAnnotation -> throw UnexpectedTypeError(
                    expectedType = "<function>", actualType = functionType
            )
            is FunctionTypeInAnnotation -> {
                var expectedArg: TypeExprInAnnotation? = functionType.argumentType
                var returnType = functionType.returnType
                for (expr in arguments) {
                    val expType = expectedArg ?: throw TooManyArgumentsError()
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
                returnType
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

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation =
            when (op) {
                SHL, SHR, USHR, XOR, LAND, LOR, MUL, DIV, MOD, PLUS, MINUS -> {
                    // int binary operators
                    val leftType = left.inferType(environment = environment)
                    if (leftType != intTypeExpr) {
                        throw UnexpectedTypeError(
                                expectedType = intTypeExpr, actualType = leftType
                        )
                    }
                    val rightType = right.inferType(environment = environment)
                    if (rightType == intTypeExpr) {
                        intTypeExpr
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = intTypeExpr, actualType = rightType
                        )
                    }
                }
                F_MUL, F_DIV, F_PLUS, F_MINUS -> {
                    // float binary operators
                    val leftType = left.inferType(environment = environment)
                    if (leftType != floatTypeExpr) {
                        throw UnexpectedTypeError(
                                expectedType = floatTypeExpr, actualType = leftType
                        )
                    }
                    val rightType = right.inferType(environment = environment)
                    if (rightType == floatTypeExpr) {
                        floatTypeExpr
                    } else {
                        throw UnexpectedTypeError(
                                expectedType = floatTypeExpr, actualType = rightType
                        )
                    }
                }
                LT, LE, GT, GE -> {
                    // comparison type operator
                    val leftType = left.inferType(environment = environment)
                    when (leftType) {
                        intTypeExpr, floatTypeExpr, charTypeExpr, stringTypeExpr -> {
                            val rightType = right.inferType(environment = environment)
                            if (leftType == rightType) {
                                boolTypeExpr
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
                    val leftType = left.inferType(environment = environment)
                    val rightType = right.inferType(environment = environment)
                    if (leftType == rightType) {
                        boolTypeExpr
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

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val t = expr.inferType(environment = environment)
        if (t == boolTypeExpr) {
            return boolTypeExpr
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

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation =
            if (environment[identifier] == null) {
                e2.inferType(environment = environment.put(
                        variable = identifier,
                        typeInfo = e1.inferType(environment = environment).asTypeInformation
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

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val functionDeclaredType = toFunctionTypeExpr(
                argumentTypes = arguments.map { it.second }, returnType = returnType
        )
        val bodyType = body.inferType(environment = environment)
        if (returnType != bodyType) {
            throw UnexpectedTypeError(expectedType = returnType, actualType = bodyType)
        }
        return functionDeclaredType
    }

}

/**
 * [IfElseExpr] represents the if else expression, guarded by [condition] and having two
 * branches [e1] and [e2].
 */
internal data class IfElseExpr(
        val condition: Expression, val e1: Expression, val e2: Expression
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val conditionType = condition.inferType(environment = environment)
        if (conditionType != boolTypeExpr) {
            throw UnexpectedTypeError(expectedType = boolTypeExpr, actualType = conditionType)
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
 * against [exprToMatch].
 */
internal data class MatchExpr(
        val exprToMatch: Expression, val matchingList: List<Pair<Pattern, Expression>>
) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val typeToMatch = exprToMatch.inferType(environment = environment)
        val typeIdentifier = (typeToMatch as? SingleIdentifierTypeInAnnotation)?.identifier
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        val typeDefinitionOpt = environment.typeDefinitions[typeIdentifier]
                as? VariantTypeInDeclaration
        val variantTypeDeclarations = typeDefinitionOpt ?.map?.toMutableMap()
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        var type: TypeExprInAnnotation? = null
        for ((pattern, expr) in matchingList) {
            if (variantTypeDeclarations.isEmpty()) {
                throw UnusedPatternError(pattern = pattern)
            }
            val newEnv: TypeCheckerEnv = when (pattern) {
                is VariantPattern -> {
                    val patternId = pattern.variantIdentifier
                    if (patternId !in variantTypeDeclarations) {
                        throw WrongPatternError(patternId = patternId)
                    }
                    variantTypeDeclarations.remove(key = patternId)
                    val associatedVarType = variantTypeDeclarations[patternId]
                    if (pattern.associatedVariable == null && associatedVarType == null) {
                        environment
                    } else if (pattern.associatedVariable != null && associatedVarType != null) {
                        environment.put(
                                variable = pattern.associatedVariable,
                                typeInfo = associatedVarType.asTypeInformation
                        )
                    } else {
                        throw WrongPatternError(patternId = patternId)
                    }
                }
                is VariablePattern -> {
                    variantTypeDeclarations.clear()
                    environment.put(
                            variable = pattern.identifier, typeInfo = typeToMatch.asTypeInformation
                    )
                }
                WildCardPattern -> {
                    variantTypeDeclarations.clear()
                    environment
                }
            }
            val exprType = expr.inferType(environment = newEnv)
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
        return type ?: throw NonExhaustivePatternMatchingError()
    }

}

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 * The throw expression is coerced to have [type].
 */
internal data class ThrowExpr(val type: TypeExprInAnnotation, val expr: Expression) : Expression() {

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val t = expr.inferType(environment = environment)
        if (t != stringTypeExpr) {
            throw UnexpectedTypeError(expectedType = stringTypeExpr, actualType = t)
        }
        return type
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

    override fun inferType(environment: TypeCheckerEnv): TypeExprInAnnotation {
        val tryType = tryExpr.inferType(environment = environment)
        val catchType = catchHandler.inferType(environment = environment.put(
                variable = exception, typeInfo = stringTypeExpr.asTypeInformation
        ))
        if (tryType != catchType) {
            throw UnexpectedTypeError(expectedType = tryType, actualType = catchType)
        }
        return tryType
    }

}
