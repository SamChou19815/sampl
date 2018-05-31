package com.developersam.pl.sapl.ast.raw

/**
 * [ModuleMembers] contains collections of different types of module members,
 * in order of declaration.
 */
internal data class ModuleMembers(
        val typeMembers: List<ModuleTypeMember>, val constantMembers: List<ModuleConstantMember>,
        val functionMembers: List<ModuleFunctionMember>, val nestedModuleMembers: List<Module>
)
