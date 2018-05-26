package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.Module

/**
 * [ModuleBuilder] builds module into AST.
 */
object ModuleBuilder : PLBaseVisitor<Module>() {

    override fun visitModuleDeclaration(ctx: PLParser.ModuleDeclarationContext): Module =
            Module(
                    name = ctx.UpperIdentifier().text,
                    members = ctx.moduleMemberDeclaration().map { it.accept(ModuleMemberBuilder) }
            )

}
