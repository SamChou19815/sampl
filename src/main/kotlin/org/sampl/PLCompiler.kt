package org.sampl

import org.sampl.codegen.ToKotlinCompiler
import org.sampl.runtime.RuntimeLibrary
import org.sampl.util.AntlrUtil

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile all the source files in the given [code] and returns
     * the compiled Kotlin code.
     * It accepts an optional [providedRuntimeLibrary] that is used as context during type checking
     * and compilation.
     */
    @JvmStatic
    fun compile(code: String, providedRuntimeLibrary: RuntimeLibrary? = null): String =
            AntlrUtil.createClassFromSource(code = code)
                    .typeCheck(providedRuntimeLibrary = providedRuntimeLibrary)
                    .let(block = ToKotlinCompiler.Companion::compile)

}
