package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.TypeIdentifier
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [TypeIdentifierBuilder] builds type identifier AST from parse tree.
 */
internal object TypeIdentifierBuilder : PLBaseVisitor<TypeIdentifier>() {

    override fun visitTypeIdentifier(ctx: PLParser.TypeIdentifierContext): TypeIdentifier =
            TypeIdentifier(
                    type = ctx.UpperIdentifier()
                            .joinToString(separator = ".", transform = TerminalNode::getText),
                    genericsList = ctx.genericsBracket().typeIdentifier()
                            .map(this::visitTypeIdentifier)
            )

}