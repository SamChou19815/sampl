package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.raw.Module

/**
 * [ModuleBuilder] builds module into AST.
 */
internal object ModuleBuilder : PLBaseVisitor<Module>() {

    override fun visitModuleDeclaration(ctx: PLParser.ModuleDeclarationContext): Module =
            Module(
                    name = ctx.UpperIdentifier().text,
                    members = ctx.moduleMembersDeclaration().accept(ModuleMembersBuilder)
            )

}
