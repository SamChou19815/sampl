package org.sampl.ast.raw

/**
 * [ClassConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 */
data class ClassConstantMember(
        override val isPublic: Boolean, val identifier: String, val expr: Expression
) : ClassMember {
    override val name: String = identifier
}
