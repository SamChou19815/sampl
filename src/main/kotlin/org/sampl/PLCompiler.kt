package org.sampl

import org.sampl.ast.raw.Clazz
import org.sampl.codegen.ToKotlinCompiler
import org.sampl.util.createClassFromDirectory
import org.sampl.util.createClassFromSource
import org.sampl.util.executeAndGetValue
import org.sampl.util.writeToFile
import java.io.File

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile the given [clazz] node.
     */
    private fun compile(clazz: Clazz) {
        val decoratedProgram = clazz.typeCheck()
        val kotlinCode = ToKotlinCompiler.compile(node = decoratedProgram)
        // Write Kotlin code to file
        File(KOTLIN_CODE_OUT_DIR).mkdirs()
        val filename = "$KOTLIN_CODE_OUT_DIR$TOP_LEVEL_PROGRAM_NAME.kt"
        writeToFile(filename = filename, content = kotlinCode)
        // Invoke Kotlin compiler
        File(JAR_OUT_DIR).mkdirs()
        val command = "kotlinc-jvm $filename $KOTLIN_COMPILER_ARGS"
        val exitValue = executeAndGetValue(command = command)
        if (exitValue != 0) {
            // If type checking and code generation work, we should not get error.
            throw RuntimeException("It should return 0, but instead we got: $exitValue.")
        }
    }

    /**
     * [compileFromSource] tries to compile all the source files in the given [code].
     */
    fun compileFromSource(code: String): Unit =
            compile(clazz = createClassFromSource(code = code))

    /**
     * [compileFromDirectory] tries to compile all the source files in the given [directory].
     */
    fun compileFromDirectory(directory: String): Unit =
            compile(clazz = createClassFromDirectory(directory = directory))

}
