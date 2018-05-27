package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import java.util.stream.Collectors

/**
 * [GenericsDeclarationBuilder] builds a generics declaration into AST fragment.
 */
internal object GenericsDeclarationBuilder : PLBaseVisitor<Set<String>>() {

    override fun visitGenericsDeclaration(ctx: PLParser.GenericsDeclarationContext): Set<String> =
            ctx.UpperIdentifier().stream().map { it.text }.collect(Collectors.toSet())

}
