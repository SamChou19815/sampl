package org.sampl

import org.sampl.ast.raw.Clazz
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.runtime.RuntimeLibrary
import org.sampl.util.AntlrUtil

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile the given [clazz] node and returns the compiled Kotlin code.
     * It accepts an optional [providedRuntimeLibrary] that is used as context during type checking
     * and compilation.
     */
    @JvmStatic
    private fun compile(clazz: Clazz, providedRuntimeLibrary: RuntimeLibrary? = null): String =
            clazz.typeCheck(providedRuntimeLibrary = providedRuntimeLibrary)
                    .let(block = ToKotlinCompiler.Companion::compile)

    /**
     * [compileFromSource] tries to compile all the source files in the given [code] and returns
     * the compiled Kotlin code.
     * It accepts an optional [providedRuntimeLibrary] that is used as context during type checking
     * and compilation.
     */
    @JvmStatic
    fun compileFromSource(code: String, providedRuntimeLibrary: RuntimeLibrary? = null): String =
            compile(
                    clazz = AntlrUtil.createClassFromSource(code = code),
                    providedRuntimeLibrary = providedRuntimeLibrary
            )

}
