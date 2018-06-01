package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.TypeDeclaration
import com.developersam.pl.sapl.ast.TypeExpr

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `type` [identifier] `=` [declaration].
 */
data class ModuleTypeMember(
        val identifier: TypeExpr.Identifier, val declaration: TypeDeclaration
) : ModuleMember {
    override val name: String = identifier.type
}
