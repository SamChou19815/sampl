package org.sampl.ast.protocol

import org.sampl.codegen.AstToCodeConverter

/**
 * [CodeConvertible] defines a set of methods that will help the conversion from this AST node to
 * the target code.
 */
interface CodeConvertible {

    fun toIndentedCode(converter: AstToCodeConverter): String

    fun toOneLineCode(converter: AstToCodeConverter): String

    fun acceptConversion(converter: AstToCodeConverter)

}
