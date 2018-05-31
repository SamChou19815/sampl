package com.developersam.pl.sapl

import com.developersam.pl.sapl.ast.raw.Module
import com.developersam.pl.sapl.modules.ModuleConstructor

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile the given [module] node.
     */
    private fun compile(module: Module) {
        val decoratedModule = module.typeCheck()
        // TODO trans-pile to kotlin code
        // TODO invoke kotlin compiler
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
