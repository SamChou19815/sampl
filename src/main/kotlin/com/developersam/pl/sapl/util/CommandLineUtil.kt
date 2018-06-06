@file:JvmName(name = "CommandLineUtil")

package com.developersam.pl.sapl.util

import java.io.IOException

/**
 * [executeAndGetValue] returns the exit value after executing [command].
 * This function has side effect beyond the control of this program. Use it with caution.
 *
 * @throws IOException if the command cannot be correctly executed.
 */
@Throws(IOException::class)
fun executeAndGetValue(command: String): Int {
    val runtime: Runtime = Runtime.getRuntime()
    return runtime.exec(command).waitFor()
}
