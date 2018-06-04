package com.developersam.pl.sapl.classes

import com.developersam.pl.sapl.antlr.PLLexer
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.raw.CompilationUnit
import com.developersam.pl.sapl.ast.raw.Clazz
import com.developersam.pl.sapl.parser.CompilationUnitBuilder
import com.developersam.pl.sapl.util.getAllSourceFiles
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * [ClassConstructor] is responsible constructing AST class under different requirements.
 */
internal object ClassConstructor {

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
     * [fromSource] tries to construct a module from the source files in the given [code].
     */
    fun fromSource(code: String): Clazz {
        val input = ByteArrayInputStream(code.toByteArray(charset = Charset.defaultCharset()))
        val unit = inputStreamToCompilationUnit(input)
        return unit.module
    }

    /**
     * [fromDirectory] tries to construct a module from all the source files in the
     * given [directory].
     */
    fun fromDirectory(directory: String): Clazz {
        val compilationUnitMap = getAllSourceFiles(directory = directory)
                .parallelStream()
                .collect(Collectors.toMap(File::nameWithoutExtension) { file ->
                    inputStreamToCompilationUnit(FileInputStream(file))
                })
        return DependencyAnalyzer.getCompilationSequence(map = compilationUnitMap)
    }

}