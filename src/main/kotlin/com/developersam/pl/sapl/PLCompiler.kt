package com.developersam.pl.sapl

import com.developersam.pl.sapl.ast.raw.Clazz
import com.developersam.pl.sapl.classes.ClassConstructor
import com.developersam.pl.sapl.codegen.ToKotlinTranspiler
import com.developersam.pl.sapl.util.executeAndGetValue
import com.developersam.pl.sapl.util.writeToFile
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
        val transpiledCode = ToKotlinTranspiler.transpile(program = decoratedProgram)
        // Write transpiled code to file
        File(KOTLIN_CODE_OUT_DIR).mkdirs()
        val filename = "$KOTLIN_CODE_OUT_DIR$TOP_LEVEL_PROGRAM_NAME.kt"
        writeToFile(filename = filename, content = transpiledCode)
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
            compile(clazz = ClassConstructor.fromSource(code = code))

    /**
     * [compileFromDirectory] tries to compile all the source files in the given [directory].
     */
    fun compileFromDirectory(directory: String): Unit =
            compile(clazz = ClassConstructor.fromDirectory(directory = directory))

}
