package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.TypeDeclaration
import com.developersam.pl.sapl.ast.TypeExpr
import com.developersam.pl.sapl.environment.TypeCheckingEnv

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `type` [identifier] `=` [declaration].
 */
data class ModuleTypeMember(
        val identifier: TypeExpr.Identifier, val declaration: TypeDeclaration
) : ModuleMember {

    override val name: String = identifier.type

    /**
     * [typeCheck] uses the given [environment] to type check this function member.
     *
     * Requires: [environment] must already put all the function members inside to allow mutually
     * recursive types.
     */
    fun typeCheck(environment: TypeCheckingEnv) {

    }

}
