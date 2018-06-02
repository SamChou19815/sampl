package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.raw.ModuleTypeMember

/**
 * [DecoratedModuleMembers] contains collections of different types of module members,
 * in order of declaration.
 */
data class DecoratedModuleMembers(
        val typeMembers: List<ModuleTypeMember>,
        val constantMembers: List<DecoratedModuleConstantMember>,
        val functionMembers: List<DecoratedModuleFunctionMember>,
        val nestedModuleMembers: List<DecoratedModule>
) {

    override fun toString(): String {
        val typeMembersStr = typeMembers.takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n  ", prefix = "  ", postfix = "\n") ?: ""
        val constantMembersStr = constantMembers.takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n  ", prefix = "  ", postfix = "\n") ?: ""
        val functionMembersStr = functionMembers.takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n  ", prefix = "  ", postfix = "\n") ?: ""
        val nestedModuleMembersStr = nestedModuleMembers.takeIf { it.isNotEmpty() }
                ?.joinToString(separator = "\n  ", prefix = "  ", postfix = "\n") ?: ""
        return typeMembersStr + constantMembersStr + functionMembersStr + nestedModuleMembersStr
    }

}
