@file:JvmName(name = "Main")

package org.sampl

import org.sampl.util.readFromFile

/**
 * [usage] contains the usage of the command line interface.
 */
private val usage: String = """
    Usage:
        -interpret <filename> # Prints the result of interpretation
        -compile <filename>   # Prints the equivalent Kotlin code as the compilation result
""".trimIndent()

/**
 * The entry point for command line interface.
 */
fun main(args: Array<String>) {
    if (args.size < 2) {
        println(usage)
    }
    val code = readFromFile(filename = args[1]) ?: run {
        println(usage)
        return
    }
    when (args[0]) {
        "-interpret" -> code.let { PLInterpreter.interpret(code = it) }.let { println(it) }
        "-compile" -> code.let { PLCompiler.compile(code = it) }.let { println(it) }
        else -> println(usage)
    }
}
