package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.codegen.TranspilerVisitor

/**
 * [DecoratedProgram] node is a top-level module with a set of ordered [members].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(val members: DecoratedModuleMembers)
    : PrettyPrintable, Transpilable {

    override fun prettyPrint(q: IndentationQueue): Unit = members.prettyPrint(q = q)

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, program = this)

}