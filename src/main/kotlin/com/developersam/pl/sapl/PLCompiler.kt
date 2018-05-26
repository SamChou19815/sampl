package com.developersam.pl.sapl

import java.io.File
import java.io.FileInputStream
import java.util.stream.Collectors

import com.developersam.pl.sapl.antlr.PLLexer
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.CompilationUnit
import com.developersam.pl.sapl.dependency.DependencyAnalyzer
import com.developersam.pl.sapl.parser.CompilationUnitBuilder
import com.developersam.pl.sapl.util.getAllSourceFiles
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

/**
 * [PLCompiler] is responsible for the compilation of all the source files in the given
 * [directory].
 *
 * @param directory the directory that contains all the source files.
 */
class PLCompiler(private val directory: String) {

    /**
     * [compile] tries to compile all the source files.
     */
    fun compile() {
        // Store everything into AST
        val compilationUnitMap: Map<String, CompilationUnit> = getAllSourceFiles(directory)
                .parallelStream()
                .collect(Collectors.toMap(File::nameWithoutExtension) { file ->
                    val inStream = ANTLRInputStream(FileInputStream(file))
                    val tokenStream = CommonTokenStream(PLLexer(inStream))
                    val parser = PLParser(tokenStream)
                    CompilationUnitBuilder.visitCompilationUnit(parser.compilationUnit())
                })
        // Construct sequence of compilation
        val compilationSequence: List<CompilationUnit> =
                DependencyAnalyzer.getCompilationSequence(map = compilationUnitMap)
        // TODO type checking

        // TODO transpile to Java code

        // TODO invoke java compiler
    }

}