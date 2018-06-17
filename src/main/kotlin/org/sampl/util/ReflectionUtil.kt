package org.sampl.util

import java.net.URLClassLoader

/**
 * [currentClassPath] returns the current class path of the program.
 */
val currentClassPath: String
    get() = (ClassLoader.getSystemClassLoader() as URLClassLoader)
            .urLs.joinToString(separator = ":") { it.path }
