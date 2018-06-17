package org.sampl.ast.decorated

import org.sampl.codegen.AstToCodeConverter
import org.sampl.codegen.CodeConvertible
import org.sampl.runtime.RuntimeLibrary

/**
 * [DecoratedProgram] node contains a set of members [members].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(
        val members: List<DecoratedClassMember>, val providedRuntimeLibrary: RuntimeLibrary?
) : CodeConvertible {

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
