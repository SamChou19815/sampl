package org.sampl.ast.raw

import org.sampl.ast.common.BinaryOperator
import org.sampl.ast.common.BinaryOperator.AND
import org.sampl.ast.common.BinaryOperator.DIV
import org.sampl.ast.common.BinaryOperator.F_DIV
import org.sampl.ast.common.BinaryOperator.F_MINUS
import org.sampl.ast.common.BinaryOperator.F_MUL
import org.sampl.ast.common.BinaryOperator.F_PLUS
import org.sampl.ast.common.BinaryOperator.GE
import org.sampl.ast.common.BinaryOperator.GT
import org.sampl.ast.common.BinaryOperator.LAND
import org.sampl.ast.common.BinaryOperator.LE
import org.sampl.ast.common.BinaryOperator.LOR
import org.sampl.ast.common.BinaryOperator.LT
import org.sampl.ast.common.BinaryOperator.MINUS
import org.sampl.ast.common.BinaryOperator.MOD
import org.sampl.ast.common.BinaryOperator.MUL
import org.sampl.ast.common.BinaryOperator.OR
import org.sampl.ast.common.BinaryOperator.PLUS
import org.sampl.ast.common.BinaryOperator.SHL
import org.sampl.ast.common.BinaryOperator.SHR
import org.sampl.ast.common.BinaryOperator.STRUCT_EQ
import org.sampl.ast.common.BinaryOperator.STRUCT_NE
import org.sampl.ast.common.BinaryOperator.STR_CONCAT
import org.sampl.ast.common.BinaryOperator.USHR
import org.sampl.ast.common.BinaryOperator.XOR
import org.sampl.ast.common.Literal
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedPattern
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeInfo
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.environment.TypeCheckingEnv
import org.sampl.exceptions.GenericsError
import org.sampl.exceptions.IdentifierError
import org.sampl.exceptions.PatternMatchingError
import org.sampl.exceptions.StructError
import org.sampl.exceptions.TooManyArgumentsError
import org.sampl.exceptions.UnexpectedTypeError
import org.sampl.exceptions.VariantNotFoundError
import org.sampl.util.inferActualGenericTypeInfo
import java.util.LinkedList

/**
 * [Expression] represents a set of supported expression.
 */
internal sealed class Expression {

    /**
     * [lineNo] reports the line number of the expression.
     */
    abstract val lineNo: Int

    /**
     * [typeCheck] returns the decorated expression with the inferred type  under the given
     * [environment].
     *
     * If the type checking failed, it should throw [UnexpectedTypeError] to indicate what's wrong.
     */
    abstract fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression

}

/**
 * [LiteralExpr] represents a [literal] as an expression at [lineNo].
 *
 * @property literal the literal object.
 */
internal data class LiteralExpr(override val lineNo: Int, val literal: Literal) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            DecoratedExpression.Literal(literal = literal, type = literal.inferredType)

}

/**
 * [VariableIdentifierExpr] represents a [variable] identifier as an expression at [lineNo].
 * It can only contain [genericInfo] which helps to determine the fixed type for this expression.
 *
 * @property variable the variable to refer to.
 * @property genericInfo a list of associated generics info, if any.
 */
internal data class VariableIdentifierExpr(
        override val lineNo: Int, val variable: String, val genericInfo: List<TypeExpr>
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        environment.normalTypeEnv[variable]?.let { typeExpr ->
            return DecoratedExpression.VariableIdentifier(
                    variable = variable, genericInfo = emptyList(),
                    isClassFunction = false, type = typeExpr
            )
        }
        // Not found in normal environment, try function environment
        val functionTypeInfo = environment.classFunctionTypeEnv[variable]
                ?: throw IdentifierError.UndefinedIdentifier(lineNo, variable)
        val genericSymbolsToSubstitute = functionTypeInfo.genericsInfo
        if (genericSymbolsToSubstitute.size != genericInfo.size) {
            throw GenericsError.GenericsInfoWrongNumberOfArguments(
                    lineNo = lineNo,
                    expectedNumber = genericSymbolsToSubstitute.size,
                    actualNumber = genericInfo.size
            )
        }
        val substitutionMap = genericSymbolsToSubstitute.zip(genericInfo).toMap()
        val type = functionTypeInfo.typeExpr.substituteGenerics(map = substitutionMap)
        return DecoratedExpression.VariableIdentifier(
                variable = variable, genericInfo = genericInfo, isClassFunction = true, type = type
        )
    }
}

/**
 * [ConstructorExpr] represents a set of constructor expression defined in type declarations.
 */
internal sealed class ConstructorExpr : Expression() {

    /**
     * [constructorTypeCheck] with environment [e] is a more constrained type check that is only
     * allowed to produce [DecoratedExpression.Constructor].
     */
    protected abstract fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedExpression.Constructor

    /**
     * @see Expression.typeCheck
     */
    final override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression =
            constructorTypeCheck(e = environment)

    /**
     * [NoArgVariant] represents a singleton value in variant with [typeName], [variantName] and
     * some potential [genericsInfo] to assist type inference at [lineNo].
     *
     * @property typeName the name of the type.
     * @property variantName the name of the variant.
     * @property genericsInfo a list of associated generics info.
     */
    data class NoArgVariant(
            override val lineNo: Int, val typeName: String, val variantName: String,
            val genericsInfo: List<TypeExpr>
    ) : ConstructorExpr() {

        /**
         * @see ConstructorExpr.constructorTypeCheck
         */
        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedExpression.Constructor {
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[typeName]
                    ?: throw IdentifierError.UndefinedTypeIdentifier(lineNo, typeName)
            val variantDeclarationMap = (typeDeclarations as? TypeDeclaration.Variant)?.map
                    ?: throw VariantNotFoundError(
                            lineNo = lineNo, typeName = typeName, variantName = variantName
                    )
            if (!variantDeclarationMap.containsKey(key = variantName)) {
                throw VariantNotFoundError(
                        lineNo = lineNo, typeName = typeName, variantName = variantName
                )
            }
            if (genericDeclarations.size != genericsInfo.size) {
                throw GenericsError.GenericsInfoWrongNumberOfArguments(
                        lineNo = lineNo, expectedNumber = genericDeclarations.size,
                        actualNumber = genericsInfo.size
                )
            }
            val type = TypeExpr.Identifier(type = typeName, genericsInfo = genericsInfo)
            return DecoratedExpression.Constructor.NoArgVariant(
                    typeName = typeName, variantName = variantName, genericsInfo = genericsInfo,
                    type = type
            )
        }

    }

    /**
     * [OneArgVariant] represents a tagged enum in variant with [typeName], [variantName] and
     * associated [data] at [lineNo].
     *
     * @property typeName the name of the type.
     * @property variantName the name of the variant.
     * @property data the data bind to the variant.
     */
    data class OneArgVariant(
            override val lineNo: Int, val typeName: String, val variantName: String,
            val data: Expression
    ) : ConstructorExpr() {

        /**
         * @see ConstructorExpr.constructorTypeCheck
         */
        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedExpression.Constructor {
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[typeName]
                    ?: throw IdentifierError.UndefinedTypeIdentifier(lineNo, typeName)
            val variantDeclarationMap = (typeDeclarations as? TypeDeclaration.Variant)?.map
                    ?: throw VariantNotFoundError(
                            lineNo = lineNo, typeName = typeName, variantName = variantName
                    )
            val declaredVariantType = variantDeclarationMap[variantName]
                    ?: throw VariantNotFoundError(
                            lineNo = lineNo, typeName = typeName, variantName = variantName
                    )
            val decoratedData = data.typeCheck(environment = e)
            val decoratedDataType = decoratedData.type
            val inferredGenericInfo: List<TypeExpr> = inferActualGenericTypeInfo(
                    genericDeclarations = genericDeclarations, lineNo = lineNo,
                    genericType = declaredVariantType, actualType = decoratedDataType
            )
            val replacementMap = genericDeclarations.zip(inferredGenericInfo).toMap()
            val expectedVariantType = declaredVariantType.substituteGenerics(map = replacementMap)
            if (expectedVariantType != decoratedDataType) {
                UnexpectedTypeError.check(
                        lineNo = lineNo, expectedType = expectedVariantType,
                        actualType = declaredVariantType
                )
            }
            val type = TypeExpr.Identifier(type = typeName, genericsInfo = inferredGenericInfo)
            return DecoratedExpression.Constructor.OneArgVariant(
                    typeName = typeName, variantName = variantName, data = decoratedData,
                    type = type
            )
        }

    }

    /**
     * [Struct] represents a struct initialization with [typeName] and initial value [declarations]
     * at [lineNo].
     *
     * @property typeName the name of the type.
     * @property declarations the declaration map of the struct.
     */
    data class Struct(
            override val lineNo: Int, val typeName: String,
            val declarations: Map<String, Expression>
    ) : ConstructorExpr() {

        /**
         * @see ConstructorExpr.constructorTypeCheck
         */
        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedExpression.Constructor {
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[typeName]
                    ?: throw IdentifierError.UndefinedTypeIdentifier(lineNo, typeName)
            val structDeclarationMap = (typeDeclarations as? TypeDeclaration.Struct)?.map
                    ?: throw StructError.NotFound(lineNo = lineNo, structName = typeName)
            val decoratedDeclarations = hashMapOf<String, DecoratedExpression>()
            val inferredGenericInfo = structDeclarationMap.map { (declaredName, declaredType) ->
                val expr = declarations[declaredName] ?: throw StructError.MissingMember(
                        lineNo = lineNo, structName = typeName, missingMember = declaredName
                )
                val decoratedExpr = expr.typeCheck(environment = e)
                decoratedDeclarations[declaredName] = decoratedExpr
                val exprType = decoratedExpr.type
                declaredType to exprType
            }.let { pairs ->
                inferActualGenericTypeInfo(
                        genericDeclarations = genericDeclarations,
                        genericTypeActualTypePairs = pairs, lineNo = lineNo
                )
            }
            val replacementMap = genericDeclarations.zip(inferredGenericInfo).toMap()
            decoratedDeclarations.forEach { name, member ->
                val expectedType = structDeclarationMap[name]?.substituteGenerics(replacementMap)
                        ?: throw StructError.NoSuchMember(
                                lineNo = lineNo, structName = typeName, memberName = name
                        )
                UnexpectedTypeError.check(
                        lineNo = lineNo, expectedType = expectedType, actualType = member.type
                )
            }
            val type = TypeExpr.Identifier(type = typeName, genericsInfo = inferredGenericInfo)
            return DecoratedExpression.Constructor.Struct(
                    typeName = typeName, declarations = decoratedDeclarations, type = type
            )
        }

    }

    /**
     * [StructWithCopy] represents a copy of [old] struct with some new values in [newDeclarations]
     * at [lineNo].
     *
     * @property old the old source struct.
     * @property newDeclarations a map of new declarations.
     */
    data class StructWithCopy(
            override val lineNo: Int, val old: Expression,
            val newDeclarations: Map<String, Expression>
    ) : ConstructorExpr() {

        /**
         * @see ConstructorExpr.constructorTypeCheck
         */
        override fun constructorTypeCheck(e: TypeCheckingEnv): DecoratedExpression.Constructor {
            val decoratedOld = old.typeCheck(environment = e)
            val expectedFinalType = decoratedOld.type as? TypeExpr.Identifier
                    ?: throw UnexpectedTypeError(
                            lineNo = lineNo, expectedType = "<struct>",
                            actualType = decoratedOld.type
                    )
            val expectedActualGenericInfo = expectedFinalType.genericsInfo
            val structName = expectedFinalType.type
            val (genericDeclarations, typeDeclarations) = e.typeDefinitions[structName]
                    ?: throw IdentifierError.UndefinedTypeIdentifier(lineNo, structName)
            val structDeclarationMap = (typeDeclarations as? TypeDeclaration.Struct)
                    ?.map
                    ?: throw StructError.NotFound(lineNo = lineNo, structName = structName)
            val replacementMap = genericDeclarations.zip(expectedActualGenericInfo).toMap()
            val actualTypeMap = structDeclarationMap.mapValues { (_, typeWithGenerics) ->
                typeWithGenerics.substituteGenerics(map = replacementMap)
            }
            val decoratedNewDeclarations = newDeclarations.mapValues { (newName, newExpr) ->
                val expectedType = actualTypeMap[newName]
                        ?: throw StructError.NoSuchMember(
                                lineNo = lineNo, structName = structName, memberName = newName
                        )
                val decoratedExpr = newExpr.typeCheck(environment = e)
                val exprType = decoratedExpr.type
                UnexpectedTypeError.check(
                        lineNo = lineNo, expectedType = expectedType, actualType = exprType
                )
                decoratedExpr
            }
            return DecoratedExpression.Constructor.StructWithCopy(
                    old = decoratedOld, newDeclarations = decoratedNewDeclarations,
                    type = expectedFinalType
            )
        }

    }

}

/**
 * [StructMemberAccessExpr] represents accessing [memberName] of [structExpr] at [lineNo].
 *
 * @property structExpr the expression for the struct.
 * @property memberName the name of the member.
 */
internal data class StructMemberAccessExpr(
        override val lineNo: Int, val structExpr: Expression, val memberName: String
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedStructExpr = structExpr.typeCheck(environment = environment)
        val structType = decoratedStructExpr.type as? TypeExpr.Identifier
                ?: throw UnexpectedTypeError(
                        lineNo = lineNo, expectedType = "<struct>",
                        actualType = decoratedStructExpr.type
                )
        val structTypeName = structType.type
        val (genericDeclarations, typeDeclarations) = environment.typeDefinitions[structTypeName]
                ?: throw IdentifierError.UndefinedTypeIdentifier(lineNo, structTypeName)
        val structDeclarationMap = (typeDeclarations as? TypeDeclaration.Struct)?.map
                ?: throw StructError.NotFound(lineNo = lineNo, structName = structTypeName)
        val memberTypeDeclaration = structDeclarationMap[memberName]
                ?: throw StructError.MissingMember(lineNo, structTypeName, memberName)
        val replacementMap = genericDeclarations.zip(structType.genericsInfo).toMap()
        val actualMemberType = memberTypeDeclaration.substituteGenerics(map = replacementMap)
        return DecoratedExpression.StructMemberAccess(
                structExpr = decoratedStructExpr, memberName = memberName, type = actualMemberType
        )
    }

}

/**
 * [NotExpr] represents the logical inversion of expression [expr] at [lineNo].
 *
 * @property expr the expression to invert.
 */
internal data class NotExpr(override val lineNo: Int, val expr: Expression) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val e = expr.typeCheck(environment = environment)
        UnexpectedTypeError.check(
                lineNo = expr.lineNo, expectedType = boolTypeExpr, actualType = e.type
        )
        return DecoratedExpression.Not(expr = e, type = boolTypeExpr)
    }

}

/**
 * [FunctionApplicationExpr] is the function application expression, with [functionExpr] as the
 * function and [arguments] as arguments of the function at [lineNo].
 *
 * @property functionExpr the function expression to apply.
 * @property arguments arguments to supply.
 */
internal data class FunctionApplicationExpr(
        override val lineNo: Int, val functionExpr: Expression, val arguments: List<Expression>
) : Expression() {

    /**
     * [getFunctionTypeInfoForParamTypeInference] returns the type information of [functionExpr] iff
     * - [functionExpr] is a variable expression.
     * - the variable refers to a class member function in [environment].
     * - the function contains generics information so type inference is not trivial.
     * If one of the conditions is not satisfied, it will return `null`.
     */
    private fun getFunctionTypeInfoForParamTypeInference(
            environment: TypeCheckingEnv
    ): Pair<String, TypeInfo>? {
        if (functionExpr !is VariableIdentifierExpr) {
            return null
        }
        if (functionExpr.genericInfo.isNotEmpty()) {
            return null
        }
        val variable = functionExpr.variable
        if (variable in environment.normalTypeEnv) {
            return null
        }
        val functionTypeInfo = environment.classFunctionTypeEnv[variable]
                ?: throw IdentifierError.UndefinedIdentifier(functionExpr.lineNo, variable)
        return functionTypeInfo.takeIf { it.genericsInfo.isNotEmpty() }?.let { variable to it }
    }

    /**
     * [typeCheckFunctionExprWithParamTypeInference] tries to type check the function expression
     * with type information from the arguments and without explicitly declared type info in
     * generics bracket.
     */
    private fun typeCheckFunctionExprWithParamTypeInference(
            variable: String, functionTypeInfo: TypeInfo,
            decoratedArguments: List<DecoratedExpression>
    ): DecoratedExpression {
        val declaredFunctionType = functionTypeInfo.typeExpr as TypeExpr.Function
        val pairs = declaredFunctionType.argumentTypes.zip(decoratedArguments.map { it.type })
        val inferredGenericsInfo =
                try {
                    inferActualGenericTypeInfo(
                            genericDeclarations = functionTypeInfo.genericsInfo,
                            genericTypeActualTypePairs = pairs, lineNo = lineNo
                    )
                } catch (e: Exception) {
                    println(pairs)
                    throw e
                }
        val replacementMap = functionTypeInfo.genericsInfo.zip(inferredGenericsInfo).toMap()
        val functionActualType = declaredFunctionType.substituteGenerics(map = replacementMap)
        return DecoratedExpression.VariableIdentifier(
                variable = variable, genericInfo = emptyList(),
                isClassFunction = true, type = functionActualType
        )
    }

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedArguments = arguments.map { it.typeCheck(environment = environment) }
        val pair = getFunctionTypeInfoForParamTypeInference(environment = environment)
        val decoratedFunctionExpr = if (pair == null) {
            functionExpr.typeCheck(environment = environment)
        } else {
            val (variable, typeInfo) = pair
            typeCheckFunctionExprWithParamTypeInference(
                    variable = variable, functionTypeInfo = typeInfo,
                    decoratedArguments = decoratedArguments
            )
        }
        val functionTypeOpt = decoratedFunctionExpr.type
        val functionType = functionTypeOpt as? TypeExpr.Function
                ?: throw UnexpectedTypeError(
                        lineNo = functionExpr.lineNo, expectedType = "<function>",
                        actualType = functionTypeOpt
                )
        val unusedArgs: LinkedList<TypeExpr> = LinkedList(functionType.argumentTypes)
        for (i in decoratedArguments.indices) {
            val decoratedExpr = decoratedArguments[i]
            if (unusedArgs.isEmpty()) {
                throw TooManyArgumentsError(lineNo = lineNo)
            }
            val expType = unusedArgs.removeFirst()
            val exprType = decoratedExpr.type
            UnexpectedTypeError.check(
                    lineNo = arguments[i].lineNo, expectedType = expType, actualType = exprType
            )
        }
        val returnType = if (unusedArgs.isEmpty()) functionType.returnType else
            TypeExpr.Function(
                    argumentTypes = ArrayList(unusedArgs), returnType = functionType.returnType
            )
        return DecoratedExpression.FunctionApplication(
                functionExpr = decoratedFunctionExpr, arguments = decoratedArguments,
                type = returnType
        )
    }

}

/**
 * [BinaryExpr] represents a binary expression with operator [op] between [left] and [right] at
 * [lineNo].
 *
 * @property left left part.
 * @property op the operator.
 * @property right right part.
 */
internal data class BinaryExpr(
        override val lineNo: Int,
        val left: Expression, val op: BinaryOperator, val right: Expression
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val leftExpr = left.typeCheck(environment = environment)
        val leftType = leftExpr.type
        val rightExpr = right.typeCheck(environment = environment)
        val rightType = rightExpr.type
        val type = when (op) {
            SHL, SHR, USHR, XOR, LAND, LOR, MUL, DIV, MOD, PLUS, MINUS -> {
                // int binary operators
                UnexpectedTypeError.check(
                        lineNo = left.lineNo, expectedType = intTypeExpr, actualType = leftType
                )
                UnexpectedTypeError.check(
                        lineNo = right.lineNo, expectedType = intTypeExpr, actualType = rightType
                )
                intTypeExpr
            }
            F_MUL, F_DIV, F_PLUS, F_MINUS -> {
                // float binary operators
                UnexpectedTypeError.check(
                        lineNo = left.lineNo, expectedType = floatTypeExpr, actualType = leftType
                )
                UnexpectedTypeError.check(
                        lineNo = right.lineNo, expectedType = floatTypeExpr, actualType = rightType
                )
                floatTypeExpr
            }
            STR_CONCAT -> {
                UnexpectedTypeError.check(
                        lineNo = left.lineNo, expectedType = stringTypeExpr, actualType = leftType
                )
                UnexpectedTypeError.check(
                        lineNo = right.lineNo, expectedType = stringTypeExpr, actualType = rightType
                )
                stringTypeExpr
            }
            LT, LE, GT, GE -> {
                // comparison type operator
                when (leftType) {
                    unitTypeExpr, intTypeExpr, floatTypeExpr, boolTypeExpr,
                    charTypeExpr, stringTypeExpr -> {
                        UnexpectedTypeError.check(
                                lineNo = right.lineNo, expectedType = leftType,
                                actualType = rightType
                        )
                        boolTypeExpr
                    }
                    else -> throw UnexpectedTypeError(
                            lineNo = left.lineNo, expectedType = intTypeExpr.toString(),
                            actualType = leftType
                    )
                }
            }
            STRUCT_EQ, STRUCT_NE -> {
                // equality operator
                UnexpectedTypeError.check(
                        lineNo = right.lineNo, expectedType = leftType, actualType = rightType
                )
                boolTypeExpr
            }
            AND, OR -> {
                // conjunction and disjunction
                UnexpectedTypeError.check(
                        lineNo = left.lineNo, expectedType = boolTypeExpr, actualType = leftType
                )
                UnexpectedTypeError.check(
                        lineNo = right.lineNo, expectedType = boolTypeExpr, actualType = rightType
                )
                boolTypeExpr
            }
        }
        return DecoratedExpression.Binary(left = leftExpr, op = op, right = rightExpr, type = type)
    }

}

/**
 * [ThrowExpr] represents the throw exception expression, where the thrown exception is [expr].
 * The throw expression is coerced to have [type] at [lineNo].
 *
 * @property expr the stuff to throw.
 * @property type type of the throw expression.
 */
internal data class ThrowExpr(
        override val lineNo: Int, val type: TypeExpr, val expr: Expression
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val e = expr.typeCheck(environment = environment)
        UnexpectedTypeError.check(
                lineNo = expr.lineNo, expectedType = stringTypeExpr, actualType = e.type
        )
        return DecoratedExpression.Throw(type = type, expr = e)
    }

}

/**
 * [IfElseExpr] represents the if else expression, guarded by [condition] and having two
 * branches [e1] and [e2] at [lineNo].
 *
 * @property condition the condition to check.
 * @property e1 expression of the first branch.
 * @property e2 expression of the second branch.
 */
internal data class IfElseExpr(
        override val lineNo: Int, val condition: Expression, val e1: Expression, val e2: Expression
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val conditionExpr = condition.typeCheck(environment = environment)
        UnexpectedTypeError.check(
                lineNo = condition.lineNo, expectedType = boolTypeExpr,
                actualType = conditionExpr.type
        )
        val decoratedE1 = e1.typeCheck(environment = environment)
        val t1 = decoratedE1.type
        val decoratedE2 = e2.typeCheck(environment = environment)
        val t2 = decoratedE2.type
        UnexpectedTypeError.check(
                lineNo = e2.lineNo, expectedType = t1, actualType = t2
        )
        return DecoratedExpression.IfElse(
                condition = conditionExpr, e1 = decoratedE1, e2 = decoratedE2, type = t1
        )
    }

}

/**
 * [MatchExpr] represents the pattern matching expression, with a list of [matchingList] to match
 * against [exprToMatch] at [lineNo].
 *
 * @property exprToMatch the expression to match.
 * @property matchingList a list of functions to match the pattern.
 */
internal data class MatchExpr(
        override val lineNo: Int, val exprToMatch: Expression,
        val matchingList: List<Pair<Pattern, Expression>>
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedExprToMatch = exprToMatch.typeCheck(environment = environment)
        val typeToMatch = decoratedExprToMatch.type
        val typeIdentifier = (typeToMatch as? TypeExpr.Identifier)
                ?: throw PatternMatchingError.UnmatchableType(exprToMatch.lineNo, typeToMatch)
        val (_, typeDefinition) = environment.typeDefinitions[typeIdentifier.type]
                ?: throw PatternMatchingError.UnmatchableType(exprToMatch.lineNo, typeToMatch)
        val variantTypeDeclarations = (typeDefinition as? TypeDeclaration.Variant)
                ?.map?.toMutableMap()
                ?: throw PatternMatchingError.UnmatchableType(exprToMatch.lineNo, typeToMatch)
        var type: TypeExpr? = null
        val decoratedMatchingList = arrayListOf<Pair<DecoratedPattern, DecoratedExpression>>()
        for ((pattern, expr) in matchingList) {
            if (variantTypeDeclarations.isEmpty()) {
                throw PatternMatchingError.UnusedPattern(lineNo = pattern.lineNo, pattern = pattern)
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
                UnexpectedTypeError.check(
                        lineNo = expr.lineNo, expectedType = knownType, actualType = exprType
                )
            }
        }
        if (variantTypeDeclarations.isNotEmpty()) {
            throw PatternMatchingError.NonExhaustive(lineNo = lineNo)
        }
        return DecoratedExpression.Match(
                exprToMatch = decoratedExprToMatch, matchingList = decoratedMatchingList,
                type = type ?: throw PatternMatchingError.NonExhaustive(lineNo = lineNo)
        )
    }

}

/**
 * [FunctionExpr] is the function expression with some [arguments] and the function [body]
 * at [lineNo].
 *
 * @property arguments a list of arguments with their types accepted by the function.
 * @property body body of the function.
 */
internal data class FunctionExpr(
        override val lineNo: Int, val arguments: List<Pair<String, TypeExpr>>, val body: Expression
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val newEnv = environment.copy(
                normalTypeEnv = arguments.fold(initial = environment.normalTypeEnv) { e, (n, t) ->
                    e.put(key = n, value = t)
                }
        )
        val bodyExpr = body.typeCheck(environment = newEnv)
        val bodyType = bodyExpr.type
        val functionDeclaredType = TypeExpr.Function(
                argumentTypes = arguments.map { it.second }, returnType = bodyType
        )
        functionDeclaredType.checkTypeValidity(environment = environment)
        return DecoratedExpression.Function(
                arguments = arguments, returnType = bodyType, body = bodyExpr,
                type = functionDeclaredType
        )
    }

}

/**
 * [TryCatchExpr] represents the try catch finally structure as an expression at [lineNo]., where
 * the [tryExpr] is evaluated, and guard by catch branch with [exception] in scope and
 * [catchHandler] to deal with it.
 *
 * @property tryExpr the expression to try.
 * @property exception the identifier for the exception.
 * @property catchHandler the code for catch.
 */
internal data class TryCatchExpr(
        override val lineNo: Int, val tryExpr: Expression, val exception: String,
        val catchHandler: Expression
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        val decoratedTryExpr = tryExpr.typeCheck(environment = environment)
        val tryType = decoratedTryExpr.type
        val decoratedCatchExpr = catchHandler.typeCheck(
                environment.copy(normalTypeEnv = environment.normalTypeEnv.put(
                        key = exception, value = stringTypeExpr
                ))
        )
        val catchType = decoratedCatchExpr.type
        UnexpectedTypeError.check(
                lineNo = catchHandler.lineNo, expectedType = tryType, actualType = catchType
        )
        return DecoratedExpression.TryCatch(
                tryExpr = decoratedTryExpr, exception = exception,
                catchHandler = decoratedCatchExpr, type = tryType
        )
    }

}

/**
 * [LetExpr] represents the let expression at [lineNo] of the form
 * `let` [identifier] `=` [e1] `;` [e2].
 * If [identifier] is `null`, it means it's a wildcard.
 *
 * @property identifier new identifier to name.
 * @property e1 the expression for the identifier.
 * @property e2 the expression after the let.
 */
internal data class LetExpr(
        override val lineNo: Int, val identifier: String?, val e1: Expression, val e2: Expression
) : Expression() {

    /**
     * @see Expression.typeCheck
     */
    override fun typeCheck(environment: TypeCheckingEnv): DecoratedExpression {
        if (identifier != null && environment.normalTypeEnv[identifier] != null) {
            throw IdentifierError.ShadowedName(lineNo, identifier)
        }
        val decoratedE1 = e1.typeCheck(environment = environment)
        val newEnv = identifier?.let { key ->
            environment.copy(normalTypeEnv = environment.normalTypeEnv.put(
                    key = key, value = decoratedE1.type
            ))
        } ?: environment
        val decoratedE2 = e2.typeCheck(environment = newEnv)
        val e2Type = decoratedE2.type
        return DecoratedExpression.Let(
                identifier = identifier, e1 = decoratedE1, e2 = decoratedE2, type = e2Type
        )
    }

}
