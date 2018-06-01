package com.developersam.pl.sapl.ast.raw

/**
 * [ModuleMember] defines the operation that all module member must support.
 */
interface ModuleMember {

    /**
     * [isPublic] reports whether the member can be accessed outside of the module.
     */
    val isPublic: Boolean

    /**
     * [name] is the name of the identifier of the module member.
     */
    val name: String

}
