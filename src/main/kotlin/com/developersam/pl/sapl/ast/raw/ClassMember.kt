package com.developersam.pl.sapl.ast.raw

/**
 * [ClassMember] defines the operation that all class member must support.
 */
interface ClassMember {

    /**
     * [isPublic] reports whether the member can be accessed outside of the class.
     */
    val isPublic: Boolean

    /**
     * [name] is the name of the identifier of the class member.
     */
    val name: String

}
