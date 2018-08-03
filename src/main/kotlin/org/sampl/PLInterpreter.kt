package org.sampl

import org.sampl.eval.Interpreter
import org.sampl.eval.Value
import org.sampl.exceptions.PLException
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
            try {
                createRawProgramFromSource(code = code)
                        .typeCheck(providedRuntimeLibrary = providedRuntimeLibrary)
                        .let { Interpreter(program = it).eval() }
            } catch (e: StackOverflowError) {
                throw PLException(m = "StackOverflow")
            } catch (e: IllegalArgumentException) {
                throw PLException(
                        m = e.message?.let { "IllegalArgumentException: $it" }
                                ?: "IllegalArgumentException"
                )
            } catch (e: ArithmeticException) {
                throw PLException(
                        m = e.message?.let { "ArithmeticException: $it" }
                                ?: "ArithmeticException"
                )
            }

}
