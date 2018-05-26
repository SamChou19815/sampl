package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLLexer
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.CompilationUnit
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream
import java.util.stream.Collectors

/**
 * [CompilationUnitBuilder] builds the compilation unit to an AST.
 */
object CompilationUnitBuilder : PLBaseVisitor<CompilationUnit>() {

    override fun visitCompilationUnit(ctx: PLParser.CompilationUnitContext): CompilationUnit =
            CompilationUnit(
                    imports = ctx.importDeclaration().UpperIdentifier()
                            .stream()
                            .map { it.text }
                            .collect(Collectors.toSet()),
                    members = ctx.moduleMemberDeclaration().map { it.accept(ModuleMemberBuilder) }
            )

    /**
     * [build] tries to build the compilation unit from an [input] that contains the source code.
     */
    fun build(input: InputStream): CompilationUnit {
        val inStream = ANTLRInputStream(input)
        val tokenStream = CommonTokenStream(PLLexer(inStream))
        val parser = PLParser(tokenStream)
        val unit = parser.compilationUnit()
        return CompilationUnitBuilder.visitCompilationUnit(unit)
    }

}
