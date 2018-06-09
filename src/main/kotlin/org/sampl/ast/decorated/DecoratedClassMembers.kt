package org.sampl.ast.decorated

import org.sampl.codegen.CodeConvertible
import org.sampl.codegen.AstToCodeConverter

/**
 * [DecoratedClassMembers] contains collections of different types of class members,
 * in order of declaration.
 */
data class DecoratedClassMembers(
        val constantMembers: List<DecoratedClassConstantMember>,
        val functionMembers: List<DecoratedClassFunctionMember>,
        val nestedClassMembers: List<DecoratedClass>
) : CodeConvertible {

    /**
     * [isEmpty] reports whether there is no actual members in this class.
     */
    val isEmpty: Boolean
        get() = constantMembers.isEmpty()
                && functionMembers.isEmpty()
                && nestedClassMembers.isEmpty()

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
