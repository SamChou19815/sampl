package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser
import org.sampl.ast.raw.ClassMembers
import org.sampl.ast.raw.Clazz
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeIdentifier
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [ClassBuilder] builds class into AST.
 */
internal object ClassBuilder : PLBaseVisitor<Clazz>() {

    override fun visitClassDeclaration(ctx: PLParser.ClassDeclarationContext): Clazz {
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
        val members = ctx.classMembersDeclaration()
                ?.map { it.accept(ClassMembersBuilder) }
                ?: emptyList()
        return Clazz(identifier = identifier, declaration = declaration, members = members)
    }


}
