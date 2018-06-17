package org.sampl

import org.sampl.eval.Interpreter
import org.sampl.eval.Value
import org.sampl.runtime.RuntimeLibrary
import org.sampl.util.createRawProgramFromSource

/**
 * [PLCompiler] is responsible for the interpretation of all the given source files.
 */
object PLInterpreter {

    /**
     * [interpret] tries to interpret all the source files in the given [code].
     * It accepts an optional [providedRuntimeLibrary] that is used as context during type checking
     * and compilation.
     */
    @JvmStatic
    fun interpret(code: String, providedRuntimeLibrary: RuntimeLibrary? = null): Value =
            createRawProgramFromSource(code = code)
                    .typeCheck(providedRuntimeLibrary = providedRuntimeLibrary)
                    .let { Interpreter(program = it).eval() }

}
