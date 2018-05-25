@file:JvmName(name = "FileUtil")

package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.common.EXTENSION
import java.io.File

/**
 * [getAllSourceFiles] returns all the source files in the given [directory].
 *
 * @param directory it must be a valid directory.
 */
fun getAllSourceFiles(directory: String): List<File> {
    val fileList = File(directory).listFiles()
    val files = arrayListOf<File>()
    for (file in fileList) {
        if (file.isFile && file.extension == EXTENSION) {
            files.add(file)
        }
    }
    return files
}
