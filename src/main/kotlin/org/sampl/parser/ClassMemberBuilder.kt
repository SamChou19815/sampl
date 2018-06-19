package org.sampl.parser

import org.antlr.v4.runtime.tree.TerminalNode
import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.ClassConstantDeclarationContext
import org.sampl.antlr.PLParser.ClassDeclarationContext
import org.sampl.antlr.PLParser.ClassFunctionDeclarationContext
import org.sampl.antlr.PLParser.ClassFunctionGroupDeclarationContext
import org.sampl.ast.raw.ClassFunction
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.sampl.ast.raw.ClassMember as M

/**
 * [ClassMemberBuilder] tries to build the class member AST node.
 */
object ClassMemberBuilder : PLBaseVisitor<M>() {

    override fun visitClassConstantDeclaration(ctx: ClassConstantDeclarationContext): M =
            M.Constant(
                    isPublic = ctx.PRIVATE() == null,
                    identifierLineNo = ctx.LowerIdentifier().symbol.line,
                    identifier = ctx.LowerIdentifier().text,
                    expr = ctx.expression().accept(ExprBuilder)
            )

    override fun visitClassFunctionGroupDeclaration(ctx: ClassFunctionGroupDeclarationContext): M =
            ctx.classFunctionDeclaration()
                    .map { it.toClassFunction() }
                    .let { M.FunctionGroup(functions = it) }

    /**
     * [ClassFunctionDeclarationContext.toClassFunction] converts the context
     * [ClassFunctionDeclarationContext] to a [ClassFunction].
     */
    private fun ClassFunctionDeclarationContext.toClassFunction(): ClassFunction =
            ClassFunction(
                    isPublic = PRIVATE() == null,
                    identifierLineNo = LowerIdentifier().symbol.line,
                    identifier = LowerIdentifier().text,
                    genericsDeclaration = genericsDeclaration()
                            ?.UpperIdentifier()?.map { it.text } ?: emptyList(),
                    arguments = argumentDeclarations().accept(ArgumentDeclarationsBuilder),
                    returnType = typeAnnotation().typeExprInAnnotation()
                            .accept(TypeExprInAnnotationBuilder),
                    body = expression().accept(ExprBuilder)
            )

    override fun visitClassDeclaration(ctx: ClassDeclarationContext): M {
        val identifier = TypeIdentifier(
                name = ctx.UpperIdentifier().text,
                genericsInfo = ctx.genericsDeclaration()
                        ?.UpperIdentifier()
                        ?.map(TerminalNode::getText)
                        ?: emptyList()
        )
        val declaration = ctx.typeExprInDeclaration()
                ?.accept(TypeExprInDeclarationBuilder)
                ?: TypeDeclaration.Struct(map = emptyMap())
        val members = ctx.classMemberDeclaration().map { it.accept(this) }
        return M.Clazz(
                identifierLineNo = ctx.UpperIdentifier().symbol.line,
                identifier = identifier, declaration = declaration, members = members
        )
    }

}
