package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.codegen.TranspilerVisitor

/**
 * [DecoratedProgram] node contains a single top-level [module].
 * It contains decorated ASTs.
 */
data class DecoratedProgram(val module: DecoratedClass) : PrettyPrintable, Transpilable {

    override fun prettyPrint(q: IndentationQueue): Unit = module.prettyPrint(q = q)

    override fun acceptTranspilation(q: IndentationQueue, visitor: TranspilerVisitor): Unit =
            visitor.visit(q = q, program = this)

}
