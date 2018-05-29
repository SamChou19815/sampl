package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.exceptions.ShadowedNameError

/**
 * [ModuleMembers] contains collections of different types of module members,
 * in order of declaration.
 */
internal data class ModuleMembers(
        val typeMembers: List<ModuleTypeMember>,
        val constantMembers: List<ModuleConstantMember>,
        val functionMembers: List<ModuleFunctionMember>,
        val nestedModuleMembers: List<Module>
) {

    /**
     * [noNameShadowingValidation] validates that the members collection has no name shadowing.
     *
     * @return [Unit]
     * @throws ShadowedNameError if there is a detected shadowed name.
     */
    fun noNameShadowingValidation() {
        val nameSetAccumulator: HashSet<String> = hashSetOf()
        val validator: (ModuleMember) -> Unit =  { member ->
            val name = member.name
            if (!nameSetAccumulator.add(name)) {
                throw ShadowedNameError(shadowedName = name)
            }
        }
        typeMembers.forEach(validator)
        constantMembers.forEach(validator)
        functionMembers.forEach(validator)
        nestedModuleMembers.forEach(validator)
    }

}

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

/**
 * [ModuleConstantMember] represents a constant declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] `=` [expr].
 */
internal data class ModuleConstantMember(
        val isPublic: Boolean, val identifier: String, val expr: Expression
) : ModuleMember {
    override val name: String = identifier
}

/**
 * [ModuleFunctionMember] represents a function declaration of the form:
 * `public/private`([isPublic]) `let` [identifier] ([genericsDeclaration])?
 * [arguments] `:` [returnType] `=` [body].
 */
internal data class ModuleFunctionMember(
        val isPublic: Boolean, val identifier: String,
        val genericsDeclaration: Set<String>,
        val arguments: List<Pair<String, TypeExprInAnnotation>>,
        val returnType: TypeExprInAnnotation, val body: Expression
) : ModuleMember {
    override val name: String = identifier
}
