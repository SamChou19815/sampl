@file:JvmName(name = "Main")

package org.sampl

import java.io.File
import java.io.IOException

/**
 * [usage] contains the usage of the command line interface.
 */
private val usage: String = """
    Usage:
        -interpret <filename> # Prints the result of interpretation
        -compile <filename>   # Prints the equivalent Kotlin code as the compilation result
""".trimIndent()

/**
 * [filenameToCodeOpt] returns the code in [filename] or `null` if there is no such file.
 */
private fun filenameToCodeOpt(filename: String): String? =
        try {
            File(filename).useLines { it.joinToString(separator = "\n") }
        } catch (e: IOException) {
            null
        }

fun main(args: Array<String>) {
    if (args.size < 2) {
        println(usage)
    }
    val code = filenameToCodeOpt(filename = args[1]) ?: run {
        println(usage)
        return
    }
    when (args[0]) {
        "-interpret" -> code.let { PLInterpreter.interpret(code = it) }.let { println(it) }
        "-compile" -> code.let { PLCompiler.compile(code = it) }.let { println(it) }
        else -> println(usage)
    }
}
