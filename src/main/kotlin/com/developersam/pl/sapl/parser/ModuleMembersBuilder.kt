package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ClassMembersDeclarationContext
import com.developersam.pl.sapl.ast.raw.ClassConstantMember
import com.developersam.pl.sapl.ast.raw.ClassFunctionMember
import com.developersam.pl.sapl.ast.raw.ClassMembers as M

/**
 * [ModuleMembersBuilder] builds module members into AST.
 */
internal object ModuleMembersBuilder : PLBaseVisitor<M>() {

    override fun visitClassMembersDeclaration(ctx: ClassMembersDeclarationContext): M {
        val constantMembers = ctx.classConstantDeclaration().map { c ->
            ClassConstantMember(
                    isPublic = c.PRIVATE() == null,
                    identifier = c.LowerIdentifier().text,
                    expr = c.expression().accept(ExprBuilder)
            )
        }
        val functionMembers = ctx.classFunctionDeclaration().map { c ->
            ClassFunctionMember(
                    isPublic = c.PRIVATE() == null,
                    identifier = c.LowerIdentifier().text,
                    genericsDeclaration = c.genericsDeclaration()
                            ?.UpperIdentifier()?.map { it.text } ?: emptyList(),
                    arguments = c.argumentDeclarations().accept(ArgumentDeclarationsBuilder),
                    returnType = c.typeAnnotation().typeExprInAnnotation()
                            .accept(TypeExprInAnnotationBuilder),
                    body = c.expression().accept(ExprBuilder)
            )
        }
        val nestedModuleMembers = ctx.classDeclaration().map { it.accept(ModuleBuilder) }
        return M(
                constantMembers = constantMembers,
                functionMembers = functionMembers,
                nestedClassMembers = nestedModuleMembers
        )
    }

}
