package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.TypeDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.environment.TypeCheckingEnv

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `public/private`([isPublic]) `type` [identifier] `=` [declaration].
 */
data class ModuleTypeMember(
        override val isPublic: Boolean,
        val identifier: TypeIdentifier, val declaration: TypeDeclaration
) : ModuleMember {

    override val name: String = identifier.name

    /**
     * [typeCheck] uses the given [environment] to type check this function member.
     *
     * Requires: [environment] must already put all the function members inside to allow mutually
     * recursive types.
     */
    fun typeCheck(environment: TypeCheckingEnv) {

    }

}
