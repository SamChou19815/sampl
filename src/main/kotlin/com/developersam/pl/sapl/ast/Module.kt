package com.developersam.pl.sapl.ast

/**
 * [Module] node has a [name] and a set of ordered [members].
 */
internal data class Module(
        override val name: String,
        val members: ModuleMembers
) : ModuleMember
