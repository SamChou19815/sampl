@file:JvmName(name = "FileUtil")

package org.sampl.util

import org.sampl.EXTENSION
import java.io.File
import java.io.IOException

/**
 * [getAllSourceFiles] returns all the source files in the given [directory].
 *
 * @param directory it must be a valid directory.
 */
internal fun getAllSourceFiles(directory: String): List<File> {
    val fileList = File(directory).listFiles()
    val files = arrayListOf<File>()
    for (file in fileList) {
        if (file.isFile && file.extension == EXTENSION) {
            files.add(file)
        }
    }
    return files
}

/**
 * [readFromFile] returns the content in [filename] or `null` if there is no such file.
 */
internal fun readFromFile(filename: String): String? =
        try {
            File(filename).useLines { it.joinToString(separator = "\n") }
        } catch (e: IOException) {
            null
        }

/**
 * [writeToFile] writes the [content] into a file with [filename].
 */
internal fun writeToFile(filename: String, content: String): Unit =
        File(filename).apply { createNewFile() }
                .writeText(content)
