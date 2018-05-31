package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.raw.ModuleTypeMember

/**
 * [DecoratedModuleMembers] contains collections of different types of module members,
 * in order of declaration.
 */
internal data class DecoratedModuleMembers(
        val typeMembers: List<ModuleTypeMember>,
        val constantMembers: List<DecoratedModuleConstantMember>,
        val functionMembers: List<DecoratedModuleFunctionMember>,
        val nestedModuleMembers: List<DecoratedModule>
)
