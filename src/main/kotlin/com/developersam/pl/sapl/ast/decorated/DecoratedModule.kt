package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.ast.protocol.TranspilerVisitor
import com.developersam.pl.sapl.codegen.IndentationQueue

/**
 * [DecoratedModule] node has a [name] and a set of ordered [members].
 * It contains decorated ASTs.
 */
data class DecoratedModule(val name: String, val members: DecoratedModuleMembers)
    : PrettyPrintable, Transpilable {

    override fun prettyPrint(q: IndentationQueue) {
        q.addLine(line = "module $name {")
        q.addEmptyLine()
        q.indentAndApply { members.prettyPrint(q = this) }
        q.addLine(line = "}")
    }

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
        visitor.visit(q = q, module = this)

}
