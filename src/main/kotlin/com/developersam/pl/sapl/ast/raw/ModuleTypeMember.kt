package com.developersam.pl.sapl.ast.raw

import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier

/**
 * [ModuleTypeMember] represents a type declaration of the form:
 * `type` [identifier] `=` [declaration].
 */
internal data class ModuleTypeMember(
        val identifier: TypeIdentifier,
        val declaration: TypeExprInDeclaration
) : ModuleMember {
    override val name: String = identifier.type
}
