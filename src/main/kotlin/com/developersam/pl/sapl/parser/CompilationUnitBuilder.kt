package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.CompilationUnit
import java.util.stream.Collectors

/**
 * [CompilationUnitBuilder] builds the compilation unit to an AST.
 */
internal object CompilationUnitBuilder : PLBaseVisitor<CompilationUnit>() {

    override fun visitCompilationUnit(ctx: PLParser.CompilationUnitContext): CompilationUnit =
            CompilationUnit(
                    imports = ctx.importDeclaration().UpperIdentifier()
                            .stream()
                            .map { it.text }
                            .collect(Collectors.toSet()),
                    members = ctx.moduleMembersDeclaration().accept(ModuleMembersBuilder)
            )

}
