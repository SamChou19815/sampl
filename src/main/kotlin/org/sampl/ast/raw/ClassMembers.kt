package org.sampl.ast.raw

/**
 * [ClassMembers] contains collections of different types of class members, in order of declaration.
 */
data class ClassMembers(
        val constantMembers: List<ClassConstantMember>,
        val functionMembers: List<ClassFunctionMember>,
        val nestedClassMembers: List<Clazz>
) {

    companion object {

        /**
         * [empty] returns an empty [ClassMembers].
         */
        val empty: ClassMembers = ClassMembers(
                constantMembers = emptyList(),
                functionMembers = emptyList(),
                nestedClassMembers = emptyList()
        )

    }

}
