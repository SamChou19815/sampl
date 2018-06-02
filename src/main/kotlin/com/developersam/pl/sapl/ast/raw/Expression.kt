package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.BinaryOperator
import com.developersam.pl.sapl.ast.BinaryOperator.AND
import com.developersam.pl.sapl.ast.BinaryOperator.DIV
import com.developersam.pl.sapl.ast.BinaryOperator.F_DIV
import com.developersam.pl.sapl.ast.BinaryOperator.F_MINUS
import com.developersam.pl.sapl.ast.BinaryOperator.F_MUL
import com.developersam.pl.sapl.ast.BinaryOperator.F_PLUS
import com.developersam.pl.sapl.ast.BinaryOperator.GE
import com.developersam.pl.sapl.ast.BinaryOperator.GT
import com.developersam.pl.sapl.ast.BinaryOperator.LAND
import com.developersam.pl.sapl.ast.BinaryOperator.LE
import com.developersam.pl.sapl.ast.BinaryOperator.LOR
import com.developersam.pl.sapl.ast.BinaryOperator.LT
import com.developersam.pl.sapl.ast.BinaryOperator.MINUS
import com.developersam.pl.sapl.ast.BinaryOperator.MOD
import com.developersam.pl.sapl.ast.BinaryOperator.MUL
import com.developersam.pl.sapl.ast.BinaryOperator.OR
import com.developersam.pl.sapl.ast.BinaryOperator.PLUS
import com.developersam.pl.sapl.ast.BinaryOperator.REF_EQ
import com.developersam.pl.sapl.ast.BinaryOperator.REF_NE
import com.developersam.pl.sapl.ast.BinaryOperator.SHL
import com.developersam.pl.sapl.ast.BinaryOperator.SHR
import com.developersam.pl.sapl.ast.BinaryOperator.STRUCT_EQ
import com.developersam.pl.sapl.ast.BinaryOperator.STRUCT_NE
import com.developersam.pl.sapl.ast.BinaryOperator.STR_CONCAT
import com.developersam.pl.sapl.ast.BinaryOperator.USHR
import com.developersam.pl.sapl.ast.BinaryOperator.XOR
import com.developersam.pl.sapl.ast.Literal
import com.developersam.pl.sapl.ast.TypeDeclaration
import com.developersam.pl.sapl.ast.TypeExpr
import com.developersam.pl.sapl.ast.boolTypeExpr
import com.developersam.pl.sapl.ast.charTypeExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedBinaryExpr
import com.developersam.pl.sapl.ast.decorated.DecoratedConstructorExpr
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
import com.developersam.pl.sapl.environment.TypeCheckingEnv
import com.developersam.pl.sapl.exceptions.GenericInfoWrongNumberOfArgumentsError
import com.developersam.pl.sapl.exceptions.NoSuchMemberInStructError
import com.developersam.pl.sapl.exceptions.NonExhaustivePatternMatchingError
import com.developersam.pl.sapl.exceptions.ShadowedNameError
import com.developersam.pl.sapl.exceptions.StructMissingMemberError
import com.developersam.pl.sapl.exceptions.StructNotFoundError
import com.developersam.pl.sapl.exceptions.TooManyArgumentsError
import com.developersam.pl.sapl.exceptions.UndefinedIdentifierError
import com.developersam.pl.sapl.exceptions.UndefinedTypeIdentifierError
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.exceptions.UnmatchableTypeError
import com.developersam.pl.sapl.exceptions.UnusedPatternError
import com.developersam.pl.sapl.exceptions.VariantNotFoundError
import com.developersam.pl.sapl.util.toFunctionTypeExpr

/**
 * [Expression] represents a set of supported expression.
 */
sealed class Expression {

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
data class LiteralExpr(val literal: Literal) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            DecoratedLiteralExpr(literal = literal, type = literal.inferredType)

}

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression.
 * It can only contain [genericInfo] which helps to determine the fixed type for this expression.
 */
data class VariableIdentifierExpr(
        val variable: String, private val genericInfo: List<TypeExpr>
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
        val type = typeInfo.typeExpr.substituteGenerics(map = substitutionMap)
        return DecoratedVariableIdentifierExpr(
                variable = variable, genericInfo = genericInfo, type = type
        )
    }

}

/**
 * [ConstructorExpr] represents a set of constructor expression defined in type declarations.
 */
sealed class ConstructorExpr : Expression() {

    /**
     * [constructorTypeCheck] with environment [e] is a more constrained type check that is only
     * allowed to produce [DecoratedConstructorExpr].
     */
    protected abstract fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedConstructorExpr

    final override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            constructorTypeCheck(e = environment)

    /**
     * [NoArgVariant] represents a singleton value in variant with [typeName], [variantName] and
     * some potential [genericInfo] to assist type inference.
     */
    data class NoArgVariant(
            val typeName: String, val variantName: String,
            val genericInfo: List<TypeExpr>
    ) : ConstructorExpr() {

        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedConstructorExpr {
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[typeName]
                    ?: throw UndefinedTypeIdentifierError(badIdentifier = typeName)
            val variantDeclarationMap = (typeDeclarations as? TypeDeclaration.Variant)?.map
                    ?: throw VariantNotFoundError(typeName = typeName, variantName = variantName)
            if (variantDeclarationMap[variantName] != null) {
                throw VariantNotFoundError(typeName = typeName, variantName = variantName)
            }
            if (genericDeclarations.size != genericInfo.size) {
                throw GenericInfoWrongNumberOfArgumentsError(
                        expectedNumber = genericDeclarations.size, actualNumber = genericInfo.size
                )
            }
            val type = TypeExpr.Identifier(type = typeName, genericsList = genericInfo)
            return DecoratedConstructorExpr.NoArgVariant(
                    typeName = typeName, variantName = variantName, genericInfo = genericInfo,
                    type = type
            )
        }

    }

    /**
     * [OneArgVariant] represents a tagged enum in variant with [typeName], [variantName] and
     * associated [data].
     */
    data class OneArgVariant(
            val typeName: String, val variantName: String, val data: Expression
    ) : ConstructorExpr() {

        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedConstructorExpr {
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[typeName]
                    ?: throw UndefinedTypeIdentifierError(badIdentifier = typeName)
            val variantDeclarationMap = (typeDeclarations as? TypeDeclaration.Variant)?.map
                    ?: throw VariantNotFoundError(typeName = typeName, variantName = variantName)
            val declaredVariantType = variantDeclarationMap[variantName]
                    ?: throw VariantNotFoundError(typeName = typeName, variantName = variantName)
            val decoratedData = data.typeCheck(environment = e)
            val decoratedDataType = decoratedData.type
            TODO(reason = "Reconcile between generic type and actual type.")
        }

    }

    /**
     * [Struct] represents a struct initialization with [typeName] and initial value [declarations].
     */
    data class Struct(
            val typeName: String, val declarations: Map<String, Expression>
    ) : ConstructorExpr() {

        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedConstructorExpr {
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[typeName]
                    ?: throw UndefinedTypeIdentifierError(badIdentifier = typeName)
            val structDeclarationMap = (typeDeclarations as? TypeDeclaration.Struct)?.map
                    ?: throw StructNotFoundError(structName = typeName)
            for ((declaredMemberName, declaredMemberType) in structDeclarationMap) {
                val expr = declarations[declaredMemberName] ?: throw StructMissingMemberError(
                        structName = typeName, missingMember = declaredMemberName
                )
                val decoratedExpr = expr.typeCheck(environment = e)
                val exprType = decoratedExpr.type

            }
            TODO(reason = "Reconcile between generic type and actual type.")
        }

    }

    /**
     * [StructWithCopy] represents a copy of [old] struct with some new values in [newDeclarations].
     */
    data class StructWithCopy(
            val old: Expression, val newDeclarations: Map<String, Expression>
    ) : ConstructorExpr() {

        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedConstructorExpr {
            val decoratedOld = old.typeCheck(environment = e)
            val expectedFinalType = decoratedOld.type as? TypeExpr.Identifier
                    ?: throw UnexpectedTypeError(expectedType = "<struct>",
                            actualType = decoratedOld.type)
            val structName = expectedFinalType.type
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[structName]
                    ?: throw UndefinedTypeIdentifierError(badIdentifier = structName)
            val structDeclarationMap = (typeDeclarations as? TypeDeclaration.Struct)?.map
                    ?: throw StructNotFoundError(structName = structName)
            for ((newMemberName, newMemberExpr) in newDeclarations) {
                val declaredMemberType = structDeclarationMap[newMemberName]
                        ?: throw NoSuchMemberInStructError(
                                structName = structName, memberName = newMemberName)
                val decoratedExpr = newMemberExpr.typeCheck(environment = e)
                val exprType = decoratedExpr.type

            }
            TODO(reason = "Reconcile between generic type and actual type.")
        }

    }

}

/**
 * [NotExpr] represents the logical inversion of expression [expr].
 */
data class NotExpr(val expr: Expression) : Expression() {

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
 * [BinaryExpr] represents a binary expression with operator [op] between [left] and [right].
 */
data class BinaryExpr(
        val left: Expression, val op: BinaryOperator, val right: Expression
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val leftExpr = left.typeCheck(environment = environment)
        val leftType = leftExpr.type
        val rightExpr = right.typeCheck(environment = environment)
        val rightType = rightExpr.type
        val type = when (op) {
            SHL, SHR, USHR, XOR, LAND, LOR, MUL, DIV, MOD, PLUS, MINUS -> {
                // int binary operators
                if (leftType != intTypeExpr) {
                    throw UnexpectedTypeError(
                            expectedType = intTypeExpr, actualType = leftType
                    )
                }
                if (rightType == intTypeExpr) intTypeExpr else {
                    throw UnexpectedTypeError(
                            expectedType = intTypeExpr, actualType = rightType
                    )
                }
            }
            F_MUL, F_DIV, F_PLUS, F_MINUS -> {
                // float binary operators
                if (leftType != floatTypeExpr) {
                    throw UnexpectedTypeError(
                            expectedType = floatTypeExpr, actualType = leftType
                    )
                }
                if (rightType == floatTypeExpr) floatTypeExpr else {
                    throw UnexpectedTypeError(
                            expectedType = floatTypeExpr, actualType = rightType
                    )
                }
            }
            STR_CONCAT -> {
                if (leftType != stringTypeExpr) {
                    throw UnexpectedTypeError(
                            expectedType = stringTypeExpr, actualType = leftType
                    )
                }
                if (leftType == rightType) stringTypeExpr else {
                    throw UnexpectedTypeError(
                            expectedType = leftType, actualType = rightType
                    )
                }
            }
            LT, LE, GT, GE -> {
                // comparison type operator
                when (leftType) {
                    intTypeExpr, floatTypeExpr, charTypeExpr, stringTypeExpr -> {
                        if (leftType == rightType) boolTypeExpr else {
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
                if (leftType == rightType) boolTypeExpr else {
                    throw UnexpectedTypeError(
                            expectedType = leftType, actualType = rightType
                    )
                }
            }
            AND, OR -> {
                // conjunction and disjunction
                if (leftType != boolTypeExpr) {
                    throw UnexpectedTypeError(
                            expectedType = boolTypeExpr, actualType = leftType
                    )
                }
                if (leftType == rightType) boolTypeExpr else {
                    throw UnexpectedTypeError(
                            expectedType = leftType, actualType = rightType
                    )
                }
            }
        }
        return DecoratedBinaryExpr(left = leftExpr, op = op, right = rightExpr, type = type)
    }

}

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 * The throw expression is coerced to have [type].
 */
data class ThrowExpr(val type: TypeExpr, val expr: Expression) : Expression() {

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
 * [IfElseExpr] represents the if else expression, guarded by [condition] and having two
 * branches [e1] and [e2].
 */
data class IfElseExpr(
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
data class MatchExpr(
        val exprToMatch: Expression, val matchingList: List<Pair<Pattern, Expression>>
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedExprToMatch = exprToMatch.typeCheck(environment = environment)
        val typeToMatch = decoratedExprToMatch.type
        val typeIdentifier = (typeToMatch as? TypeExpr.Identifier)
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        val (_, typeDefinition) = environment.typeDefinitions[typeIdentifier.type]
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        val variantTypeDeclarations = (typeDefinition as? TypeDeclaration.Variant)
                ?.map?.toMutableMap()
                ?: throw UnmatchableTypeError(typeExpr = typeToMatch)
        var type: TypeExpr? = null
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
 * [LetExpr] represents the let expression of the form
 * `let` [identifier] `=` [e1] `;` [e2]
 */
data class LetExpr(
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
data class FunctionExpr(
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: Expression
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val functionDeclaredType = toFunctionTypeExpr(
                argumentTypes = arguments.map { it.second }, returnType = returnType
        )
        functionDeclaredType.checkTypeValidity(environment = environment)
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
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function.
 */
data class FunctionApplicationExpr(
        val functionExpr: Expression, val arguments: List<Expression>
) : Expression() {

    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedFunctionExpr = functionExpr.typeCheck(environment = environment)
        val functionTypeOpt = decoratedFunctionExpr.type
        val functionType = functionTypeOpt as? TypeExpr.Function
                ?: throw UnexpectedTypeError(
                        expectedType = "<function>", actualType = functionTypeOpt
                )
        var expectedArg: TypeExpr? = functionType.argumentType
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
                is TypeExpr.Identifier -> {
                    expectedArg = null
                }
                is TypeExpr.Function -> {
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

/**
 * [TryCatchExpr] represents the try catch finally structure as an expression, where the
 * [tryExpr] is evaluated, and guard by catch branch with [exception] in scope and [catchHandler]
 * to deal with it.
 */
data class TryCatchExpr(
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
