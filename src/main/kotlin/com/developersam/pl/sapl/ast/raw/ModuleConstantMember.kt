package com.developersam.pl.sapl.ast.raw

/**
 * [ModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 */
data class ModuleConstantMember(
        val isPublic: Boolean, val identifier: String, val expr: Expression
) : ModuleMember {
    override val name: String = identifier
}
