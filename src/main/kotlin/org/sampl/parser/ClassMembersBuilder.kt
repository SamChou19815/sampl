package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.ClassMembersDeclarationContext
import org.sampl.ast.raw.ClassConstantMember
import org.sampl.ast.raw.ClassFunctionMember
import org.sampl.ast.raw.ClassMembers as M

/**
 * [ClassMembersBuilder] builds class members into AST.
 */
internal object ClassMembersBuilder : PLBaseVisitor<M>() {

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
        val nestedModuleMembers = ctx.classDeclaration().map { it.accept(ClassBuilder) }
        return M(constantMembers, functionMembers, nestedModuleMembers)
    }

}
