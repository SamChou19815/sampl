package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.decorated.DecoratedModuleFunctionMember
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.environment.TypeCheckingEnv
import com.developersam.pl.sapl.exceptions.UnexpectedTypeError
import com.developersam.pl.sapl.util.toFunctionTypeExpr

/**
 * [ModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 */
data class ModuleFunctionMember(
        override val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: List<String>,
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: Expression
) : ModuleMember {

    override val name: String = identifier

    /**
     * [functionType] reports the functional type of itself.
     */
    val functionType: TypeExpr.Function = toFunctionTypeExpr(
            argumentTypes = arguments.map { it.second },
            returnType = returnType
    )

    /**
     * [typeCheck] uses the given [environment] to type check this function member and returns
     * an [DecoratedModuleFunctionMember] with inferred type.
     *
     * Requires: [environment] must already put all the function members inside to allow mutually
     * recursive functions.
     */
    fun typeCheck(environment: TypeCheckingEnv): DecoratedModuleFunctionMember {
        val genericsDeclarationAndArgsAddedEnv = environment.copy(
                declaredTypes = genericsDeclaration.fold(environment.declaredTypes) { acc, s ->
                    acc.put(key = s, value = emptyList())
                },
                typeEnv = arguments.fold(environment.typeEnv) { acc, (name, type) ->
                    acc.put(key = name, value = type.asTypeInformation)
                }
        )
        functionType.checkTypeValidity(environment = genericsDeclarationAndArgsAddedEnv)
        val expectedType = returnType
        val bodyExpr = body.typeCheck(environment = genericsDeclarationAndArgsAddedEnv)
        val bodyType = bodyExpr.type
        if (expectedType != bodyType) {
            throw UnexpectedTypeError(expectedType = expectedType, actualType = bodyType)
        }
        return DecoratedModuleFunctionMember(
                isPublic = isPublic, identifier = identifier,
                genericsDeclaration = genericsDeclaration, arguments = arguments,
                returnType = returnType, body = bodyExpr, type = functionType
        )
    }

}
