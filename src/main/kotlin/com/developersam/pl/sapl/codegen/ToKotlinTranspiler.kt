package com.developersam.pl.sapl.codegen

import com.developersam.pl.sapl.ast.decorated.DecoratedProgram
import com.developersam.pl.sapl.codegen.KotlinTranspilerVisitor.visit

object ToKotlinTranspiler {

    fun transpile(program: DecoratedProgram): String =
            IndentationQueue(strategy = KotlinTranspilerVisitor.indentationStrategy)
                    .apply { visit(q = this, program = program) }.toIndentedCode()

}
