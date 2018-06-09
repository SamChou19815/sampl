package org.sampl.codegen

import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.codegen.KotlinTranspilerVisitor.visit

object ToKotlinTranspiler {

    fun transpile(program: DecoratedProgram): String =
            IndentationQueue(strategy = KotlinTranspilerVisitor.indentationStrategy)
                    .apply { visit(q = this, program = program) }.toIndentedCode()

}
