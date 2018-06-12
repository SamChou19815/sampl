package org.sampl.ast.decorated

import org.sampl.codegen.AstToCodeConverter
import org.sampl.codegen.CodeConvertible
import org.sampl.runtime.RuntimeLibrary

/**
 * [DecoratedProgram] node contains a single top-level class [clazz].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(
        val clazz: DecoratedClass, val providedRuntimeLibrary: RuntimeLibrary? = null
) : CodeConvertible {

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
