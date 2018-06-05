package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.raw.ClassMembers
import com.developersam.pl.sapl.ast.raw.Clazz
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeIdentifier
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
                ?.accept(ClassMembersBuilder)
                ?: ClassMembers.empty
        return Clazz(identifier = identifier, declaration = declaration, members = members)
    }


}
