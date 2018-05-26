package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ModuleConstantDeclarationContext
import com.developersam.pl.sapl.antlr.PLParser.ModuleFunctionDeclarationContext
import com.developersam.pl.sapl.antlr.PLParser.ModuleTypeDeclarationContext
import com.developersam.pl.sapl.antlr.PLParser.NestedModuleDeclarationContext
import com.developersam.pl.sapl.ast.ModuleConstantMember
import com.developersam.pl.sapl.ast.ModuleFunctionMember
import com.developersam.pl.sapl.ast.ModuleTypeMember
import com.developersam.pl.sapl.ast.NestedModule
import com.developersam.pl.sapl.ast.ModuleMember as M

/**
 * [ModuleBuilder] builds module members into AST.
 */
object ModuleMemberBuilder : PLBaseVisitor<M>() {

    override fun visitNestedModuleDeclaration(ctx: NestedModuleDeclarationContext): M =
            NestedModule(module = ctx.moduleDeclaration().accept(ModuleBuilder))

    override fun visitModuleTypeDeclaration(ctx: ModuleTypeDeclarationContext): M =
            ModuleTypeMember(
                    isPublic = ctx.PRIVATE() == null,
                    identifier = ctx.typeIdentifier().accept(TypeIdentifierBuilder),
                    declaration = ctx.typeExprInDeclaration().accept(TypeExprInDeclarationBuilder)
            )

    override fun visitModuleConstantDeclaration(ctx: ModuleConstantDeclarationContext): M =
            ModuleConstantMember(
                    isPublic = ctx.PRIVATE() == null,
                    identifier = ctx.LowerIdentifier().text,
                    expr = ctx.expression().accept(ExprBuilder)
            )

    override fun visitModuleFunctionDeclaration(ctx: ModuleFunctionDeclarationContext): M =
            ModuleFunctionMember(
                    isPublic = ctx.PRIVATE() == null,
                    identifier = ctx.LowerIdentifier().text,
                    genericsDeclaration = ctx.genericsDeclaration()
                            ?.accept(GenericsDeclarationBuilder) ?: emptySet(),
                    arguments = ctx.argumentDeclaration()
                            .map { it.accept(ArgumentDeclarationBuilder) },
                    returnType = ctx.typeAnnotation().typeExprInAnnotation()
                            .accept(TypeExprInAnnotationBuilder),
                    body = ctx.expression().accept(ExprBuilder)
            )

}
