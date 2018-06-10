@file:JvmName(name = "AntlrUtil")

package org.sampl.util

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.sampl.antlr.PLLexer
import org.sampl.antlr.PLParser
import org.sampl.ast.raw.Clazz
import org.sampl.ast.raw.CompilationUnit
import org.sampl.parser.CompilationUnitBuilder
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * [inputStreamToCompilationUnit] tries to build the compilation unit from an [inputStream] that
 * contains the source code.
 */
private fun inputStreamToCompilationUnit(inputStream: InputStream): CompilationUnit {
    val inStream = ANTLRInputStream(inputStream)
    val tokenStream = CommonTokenStream(PLLexer(inStream))
    val parser = PLParser(tokenStream)
    val unit = parser.compilationUnit()
    return CompilationUnitBuilder.visitCompilationUnit(unit)
}

/**
 * [createClassFromSource] tries to create a class from the source files in the given [code].
 */
internal fun createClassFromSource(code: String): Clazz {
    val input = ByteArrayInputStream(code.toByteArray(charset = Charset.defaultCharset()))
    val unit = inputStreamToCompilationUnit(input)
    return unit.clazz
}

/**
 * [createClassFromDirectory] tries to create a class from all the source files in the
 * given [directory].
 */
internal fun createClassFromDirectory(directory: String): Clazz {
    val compilationUnitMap = getAllSourceFiles(directory = directory)
            .parallelStream()
            .collect(Collectors.toMap(File::nameWithoutExtension) { file ->
                inputStreamToCompilationUnit(FileInputStream(file))
            })
    return createCompilationSequence(map = compilationUnitMap)
}
