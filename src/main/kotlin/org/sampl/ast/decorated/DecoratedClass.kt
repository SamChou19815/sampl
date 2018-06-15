package org.sampl.ast.decorated

import org.sampl.codegen.CodeConvertible
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.sampl.codegen.AstToCodeConverter

/**
 * [DecoratedClass] node has an type identifier with generics [identifier], a type [declaration]
 * and a set of ordered [members].
 * It contains decorated ASTs.
 */
data class DecoratedClass(
        val identifier: TypeIdentifier,
        val declaration: TypeDeclaration,
        val members: List<DecoratedClassMembers>
) : CodeConvertible {

    override fun acceptConversion(converter: AstToCodeConverter): Unit =
            converter.convert(node = this)

}
