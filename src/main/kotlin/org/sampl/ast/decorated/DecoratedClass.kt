package org.sampl.ast.decorated

import org.sampl.ast.protocol.PrettyPrintable
import org.sampl.ast.protocol.Transpilable
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.sampl.codegen.IdtQueue
import org.sampl.codegen.TranspilerVisitor

/**
 * [DecoratedClass] node has an type identifier with generics [identifier], a type [declaration]
 * and a set of ordered [members].
 * It contains decorated ASTs.
 */
data class DecoratedClass(
        val identifier: TypeIdentifier,
        val declaration: TypeDeclaration,
        val members: DecoratedClassMembers
) : PrettyPrintable, Transpilable {

    override fun prettyPrint(q: IdtQueue) {
        if (declaration.isEmpty && members.isEmpty) {
            q.addLine(line = "class $identifier")
            return
        } else if (declaration.isEmpty) {
            q.addLine(line = "class $identifier {")
            q.addEmptyLine()
            q.indentAndApply { members.prettyPrint(q = this) }
            q.addLine(line = "}")
            return
        } else if (members.isEmpty) {
            q.addLine(line = "class $identifier (")
            q.indentAndApply { declaration.prettyPrint(q = this) }
            q.addLine(line = ")")
        } else {
            q.addLine(line = "class $identifier (")
            q.indentAndApply { declaration.prettyPrint(q = this) }
            q.addLine(line = ") {")
            q.addEmptyLine()
            q.indentAndApply { members.prettyPrint(q = this) }
            q.addLine(line = "}")
        }
    }

    override fun acceptTranspilation(q: IdtQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, clazz = this)

}
