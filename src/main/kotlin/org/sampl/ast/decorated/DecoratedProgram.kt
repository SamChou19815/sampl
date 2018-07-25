package org.sampl.ast.decorated

import org.sampl.codegen.AstToCodeConverter
import org.sampl.codegen.CodeConvertible
import org.sampl.runtime.RuntimeLibrary

/**
 * [DecoratedProgram] node contains a set of members [members].
 * It contains decorated ASTs.
 *
 * @property members a list of class members.
 * @property providedRuntimeLibrary the provided library for execution at runtime.
 */
internal data class DecoratedProgram(
        val members: List<DecoratedClassMember>, val providedRuntimeLibrary: RuntimeLibrary?
) : CodeConvertible {

    /**
     * @see CodeConvertible.acceptConversion
     */
    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
