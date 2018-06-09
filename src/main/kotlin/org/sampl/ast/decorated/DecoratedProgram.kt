package org.sampl.ast.decorated

import org.sampl.codegen.CodeConvertible
import org.sampl.codegen.AstToCodeConverter

/**
 * [DecoratedProgram] node contains a single top-level class [clazz].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(val clazz: DecoratedClass) : CodeConvertible {

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
