package com.developersam.pl.sapl.util

import com.developersam.pl.sapl.EXTENSION
import java.io.File

/**
 * [FileUtil] contains a set of source code file related utilities.
 */
internal object FileUtil {

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

}
