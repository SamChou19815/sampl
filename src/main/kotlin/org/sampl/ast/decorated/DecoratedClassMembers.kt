package org.sampl.ast.decorated

import org.sampl.ast.protocol.PrettyPrintable
import org.sampl.ast.protocol.Transpilable
import org.sampl.codegen.IndentationQueue
import org.sampl.codegen.TranspilerVisitor

/**
 * [DecoratedClassMembers] contains collections of different types of class members,
 * in order of declaration.
 */
data class DecoratedClassMembers(
        val constantMembers: List<DecoratedClassConstantMember>,
        val functionMembers: List<DecoratedClassFunctionMember>,
        val nestedClassMembers: List<DecoratedClass>
) : PrettyPrintable, Transpilable {

    /**
     * [isEmpty] reports whether there is no actual members in this class.
     */
    val isEmpty: Boolean
        get() = constantMembers.isEmpty()
                && functionMembers.isEmpty()
                && nestedClassMembers.isEmpty()

    override fun prettyPrint(q: IndentationQueue) {
        val printerAction: (PrettyPrintable) -> Unit = { m ->
            m.prettyPrint(q = q)
            q.addEmptyLine()
        }
        constantMembers.forEach(action = printerAction)
        functionMembers.forEach(action = printerAction)
        nestedClassMembers.forEach(action = printerAction)
    }

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, members = this)

}
