package com.developersam.pl.sapl

import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.modules.ModuleConstructor
import com.developersam.pl.sapl.typecheck.TypeChecker

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile the given [module] node.
     */
    private fun compile(module: Module) {
        // Type checking
        TypeChecker.typeCheck(module = module)
        // TODO trans-pile to Java code

        // TODO invoke java compiler

    }

    /**
     * [compileFromSource] tries to compile all the source files in the given [code].
     */
    fun compileFromSource(code: String): Unit =
            compile(module = ModuleConstructor.fromSource(code = code))

    /**
     * [compileFromDirectory] tries to compile all the source files in the given [directory].
     */
    fun compileFromDirectory(directory: String): Unit =
            compile(module = ModuleConstructor.fromDirectory(directory = directory))

}
