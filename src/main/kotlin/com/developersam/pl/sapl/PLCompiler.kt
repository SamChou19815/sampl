package com.developersam.pl.sapl

import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.dependency.DependencyAnalyzer
import com.developersam.pl.sapl.parser.CompilationUnitBuilder
import com.developersam.pl.sapl.typecheck.TypeChecker
import com.developersam.pl.sapl.util.FileUtil
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 * [PLCompiler] is responsible for the compilation of all the given source files.
 */
object PLCompiler {

    /**
     * [compile] tries to compile the given [module] node.
     */
    private fun compile(module: Module) {
        // Type checking
        TypeChecker.typeCheck(module = module)
        // TODO trans-pile to Java code

        // TODO invoke java compiler

    }

    /**
     * [compileFromSource] tries to compile all the source files in the given [code].
     */
    fun compileFromSource(code: String) {
        val input = ByteArrayInputStream(code.toByteArray(charset = Charset.defaultCharset()))
        val unit = CompilationUnitBuilder.build(input)
        val module = Module(name = "Main", members = unit.members)
        compile(module = module)
    }

    /**
     * [compileFromDirectory] tries to compile all the source files in the given [directory].
     */
    fun compileFromDirectory(directory: String) {
        val compilationUnitMap = FileUtil.getAllSourceFiles(directory = directory)
                .parallelStream()
                .collect(Collectors.toMap(File::nameWithoutExtension) { file ->
                    CompilationUnitBuilder.build(FileInputStream(file))
                })
        val module = DependencyAnalyzer.getCompilationSequence(map = compilationUnitMap)
        compile(module = module)
    }

}
