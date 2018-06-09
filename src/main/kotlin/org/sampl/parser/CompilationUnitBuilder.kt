package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser
import org.sampl.ast.raw.CompilationUnit
import java.util.stream.Collectors

/**
 * [CompilationUnitBuilder] builds the compilation unit to an AST.
 */
internal object CompilationUnitBuilder : PLBaseVisitor<CompilationUnit>() {

    override fun visitCompilationUnit(ctx: PLParser.CompilationUnitContext): CompilationUnit =
            CompilationUnit(
                    imports = ctx.importDeclaration()?.UpperIdentifier()
                            ?.stream()
                            ?.map { it.text }
                            ?.collect(Collectors.toSet())
                            ?: emptySet(),
                    clazz = ctx.classDeclaration().accept(ClassBuilder)
            )

}
