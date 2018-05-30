package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.exceptions.ShadowedNameError

/**
 * [Module] node has a [name] and a set of ordered [members].
 */
internal data class Module(
        override val name: String,
        val members: ModuleMembers
) : ModuleMember {

    /**
     * [noNameShadowingValidation] validates that the members collection has no name shadowing by
     * checking whether there is a name conflict with a name in [set].
     *
     * @return [Unit]
     * @throws ShadowedNameError if there is a detected shadowed name.
     */
    private fun noNameShadowingValidation(set: HashSet<String>) {
        if (!set.add(name)) {
            throw ShadowedNameError(shadowedName = name)
        }
        val validator: (ModuleMember) -> Unit = { member ->
            val name = member.name
            if (!set.add(name)) {
                throw ShadowedNameError(shadowedName = name)
            }
        }
        members.typeMembers.forEach(validator)
        members.constantMembers.forEach(validator)
        members.functionMembers.forEach(validator)
        members.nestedModuleMembers.forEach { it.noNameShadowingValidation(set = set) }
    }

    /**
     * [noNameShadowingValidation] validates that the members collection has no name shadowing.
     *
     * @return [Unit]
     * @throws ShadowedNameError if there is a detected shadowed name.
     */
    fun noNameShadowingValidation(): Unit = noNameShadowingValidation(set = hashSetOf())

}
