package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.codegen.TranspilerVisitor

/**
 * [DecoratedProgram] node contains a single top-level class [clazz].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(val clazz: DecoratedClass) : PrettyPrintable, Transpilable {

    override fun prettyPrint(q: IndentationQueue): Unit = clazz.prettyPrint(q = q)

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, program = this)

}
