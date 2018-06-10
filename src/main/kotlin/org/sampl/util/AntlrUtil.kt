package org.sampl.util

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.sampl.antlr.PLLexer
import org.sampl.antlr.PLParser
import org.sampl.ast.raw.Clazz
import org.sampl.ast.raw.CompilationUnit
import org.sampl.parser.CompilationUnitBuilder
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset

/**
 * [AntlrUtil] defines a set of utility functions related to interop with ANTLR.
 */
internal object AntlrUtil {

    /**
     * [InputStream.toCompilationUnit] tries to build the compilation unit from an input stream that
     * contains the source code.
     */
    @JvmStatic
    private fun InputStream.toCompilationUnit(): CompilationUnit {
        val inStream = ANTLRInputStream(this)
        val tokenStream = CommonTokenStream(PLLexer(inStream))
        val parser = PLParser(tokenStream)
        val unit = parser.compilationUnit()
        return CompilationUnitBuilder.visitCompilationUnit(unit)
    }

    /**
     * [createClassFromSource] tries to create a class from the source files in the given [code].
     */
    @JvmStatic
    fun createClassFromSource(code: String): Clazz {
        val input = ByteArrayInputStream(code.toByteArray(charset = Charset.defaultCharset()))
        return input.toCompilationUnit().clazz
    }

    /**
     * [createClassFromDirectory] tries to create a class from all the source files in the given
     * [directory].
     */
    @JvmStatic
    fun createClassFromDirectory(directory: String): Clazz =
            getAllSourceFiles(directory = directory)
                    .asSequence()
                    .map { it.nameWithoutExtension to FileInputStream(it).toCompilationUnit() }
                    .toMap()
                    .let { createCompilationSequence(map = it) }

}
