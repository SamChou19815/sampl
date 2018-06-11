package org.sampl

import org.sampl.ast.raw.Clazz
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.runtime.PrimitiveRuntimeLibrary
import org.sampl.runtime.toAnnotatedFunctions
import org.sampl.util.AntlrUtil

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile the given [clazz] node and returns the compiled Kotlin code.
     */
    @JvmStatic
    private fun compile(clazz: Clazz): String {
        return ToKotlinCompiler.compile(node = clazz.typeCheck())
    }

    /**
     * [compileFromSource] tries to compile all the source files in the given [code] and returns
     * the compiled Kotlin code.
     */
    @JvmStatic
    fun compileFromSource(code: String): String =
            compile(clazz = AntlrUtil.createClassFromSource(code = code))

}
