package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ModuleMembersDeclarationContext
import com.developersam.pl.sapl.ast.raw.ModuleConstantMember
import com.developersam.pl.sapl.ast.raw.ModuleFunctionMember
import com.developersam.pl.sapl.ast.raw.ModuleTypeMember
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import org.antlr.v4.runtime.tree.TerminalNode
import com.developersam.pl.sapl.ast.raw.ModuleMembers as M

/**
 * [ModuleMembersBuilder] builds module members into AST.
 */
internal object ModuleMembersBuilder : PLBaseVisitor<M>() {

    override fun visitModuleMembersDeclaration(ctx: ModuleMembersDeclarationContext): M {
        val typeMembers = ctx.moduleTypeDeclaration().map { c ->
            ModuleTypeMember(
                    isPublic = c.PRIVATE() == null,
                    identifier = TypeIdentifier(
                            name = c.UpperIdentifier().text,
                            genericsInfo = c.genericsDeclaration()
                                    ?.UpperIdentifier()
                                    ?.map(TerminalNode::getText)
                                    ?: emptyList()
                    ),
                    declaration = c.typeExprInDeclaration().accept(TypeExprInDeclarationBuilder)
            )
        }
        val constantMembers = ctx.moduleConstantDeclaration().map { c ->
            ModuleConstantMember(
                    isPublic = c.PRIVATE() == null,
                    identifier = c.LowerIdentifier().text,
                    expr = c.expression().accept(ExprBuilder)
            )
        }
        val functionMembers = ctx.moduleFunctionDeclaration().map { c ->
            ModuleFunctionMember(
                    isPublic = c.PRIVATE() == null,
                    identifier = c.LowerIdentifier().text,
                    genericsDeclaration = c.genericsDeclaration()
                            ?.UpperIdentifier()?.map { it.text } ?: emptyList(),
                    arguments = c.argumentDeclaration()
                            .map { it.accept(ArgumentDeclarationBuilder) },
                    returnType = c.typeAnnotation().typeExprInAnnotation()
                            .accept(TypeExprInAnnotationBuilder),
                    body = c.expression().accept(ExprBuilder)
            )
        }
        val nestedModuleMembers = ctx.moduleDeclaration().map { it.accept(ModuleBuilder) }
        return M(
                typeMembers = typeMembers, constantMembers = constantMembers,
                functionMembers = functionMembers, nestedModuleMembers = nestedModuleMembers
        )
    }

}
