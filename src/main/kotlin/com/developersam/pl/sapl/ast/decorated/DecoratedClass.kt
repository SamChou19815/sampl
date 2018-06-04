package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.codegen.TranspilerVisitor

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

    override fun prettyPrint(q: IndentationQueue) {
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

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, clazz = this)

}
