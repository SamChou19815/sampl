package com.developersam.pl.sapl

import com.developersam.pl.sapl.antlr.LanguageLexer
import com.developersam.pl.sapl.antlr.LanguageParser
import com.developersam.pl.sapl.antlr.LanguageParser.CompilationUnitContext
import com.developersam.pl.sapl.util.DependencyAnalyzer
import com.developersam.pl.sapl.util.getAllSourceFiles
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.FileInputStream
import java.util.stream.Collectors

/**
 * [LanguageCompiler] is responsible for the compilation of all the source files in the given
 * [directory].
 *
 * @param directory the directory that contains all the source files.
 */
class LanguageCompiler(private val directory: String) {

    /**
     * [compile] tries to compile all the source files.
     */
    fun compile() {
        // Store everything into AST
        val compilationUnitMap = getAllSourceFiles(directory)
                .parallelStream()
                .collect(Collectors.toMap(File::nameWithoutExtension) { file ->
                    val inStream = ANTLRInputStream(FileInputStream(file))
                    val tokenStream = CommonTokenStream(LanguageLexer(inStream))
                    val parser = LanguageParser(tokenStream)
                    parser.compilationUnit()
                })
        // Build Dependency Graph
        val dependencyGraph: HashMap<String, Set<String>> = hashMapOf()
        for ((filename, compilationUnit) in compilationUnitMap) {
            val importDeclarationContext = compilationUnit.importDeclaration()
            val dependsOn: Set<String> = importDeclarationContext
                    ?.UpperIdentifier()
                    ?.stream()
                    ?.map { it.symbol.text }
                    ?.collect(Collectors.toSet())
                    ?: emptySet()
            dependencyGraph[filename] = dependsOn
        }
        // Construct sequence of compilation
        val compilationSequence: List<CompilationUnitContext> = DependencyAnalyzer(dependencyGraph)
                .sortedList
                ?.map { compilationUnitMap[it]!! }
                ?: throw CompileTimeError(message = "Cyclic Dependency Detected!")
        // TODO type checking

        // TODO transpile to Java code

        // TODO invoke java compiler
    }

}