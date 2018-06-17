package org.sampl.util

import java.io.IOException

/**
 * [executeAndGetValue] returns the exit value after executing [command].
 * This function has side effect beyond the control of this program. Use it with caution.
 *
 * @throws IOException if the command cannot be correctly executed.
 */
@Throws(IOException::class)
fun executeAndGetValue(command: String): Triple<Int, String, String> {
    val runtime: Runtime = Runtime.getRuntime()
    val process: Process = runtime.exec(command)
    val normalOutput = process.inputStream.bufferedReader()
            .lineSequence().joinToString(separator = "\n")
    val errorOutput = process.errorStream.bufferedReader()
            .lineSequence().joinToString(separator = "\n")
    val exitCode = process.waitFor()
    return Triple(first = exitCode, second = normalOutput, third = errorOutput)
}
