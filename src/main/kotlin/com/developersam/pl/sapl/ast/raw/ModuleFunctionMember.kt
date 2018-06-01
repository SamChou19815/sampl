package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.TypeExpr
import com.developersam.pl.sapl.util.toFunctionTypeExpr

/**
 * [ModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 */
data class ModuleFunctionMember(
        val isPublic: Boolean, val identifier: String,
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

}
