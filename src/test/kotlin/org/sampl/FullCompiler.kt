package org.sampl

import org.sampl.util.executeAndGetValue
import org.sampl.util.writeToFile
import java.io.File

/**
 * [FullCompiler] is the compiler used in the test that calls Kotlin compiler directly to finish
 * the compilation pipeline.
 */
object FullCompiler {

    /**
     * [compile] tries to compile the given [code] to JVM bytecode directly.
     */
    @JvmStatic
    fun compile(code: String) {
        val kotlinCode = PLCompiler.compileFromSource(code = code)
        // Write Kotlin code to file
        File(KOTLIN_CODE_OUT_DIR).mkdirs()
        val filename = "$KOTLIN_CODE_OUT_DIR$TOP_LEVEL_PROGRAM_NAME.kt"
        writeToFile(filename = filename, content = kotlinCode)
        // Invoke Kotlin compiler
        val command = "kotlinc-jvm $filename $kotlinCompilerArgs"
        val exitValue = executeAndGetValue(command = command)
        if (exitValue != 0) {
            // If type checking and code generation work, we should not get error.
            throw RuntimeException("It should return 0, but instead we got: $exitValue.")
        }
    }

}
