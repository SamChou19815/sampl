package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser
import com.developersam.pl.sapl.ast.TypeIdentifier
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * [TypeIdentifierBuilder] builds type identifier AST from parse tree.
 */
internal object TypeIdentifierBuilder : PLBaseVisitor<TypeIdentifier>() {

    override fun visitTypeIdentifier(ctx: PLParser.TypeIdentifierContext): TypeIdentifier {
        val ids = ctx.UpperIdentifier()
        val l = ids.size
        return TypeIdentifier(
                moduleChain = ids.subList(fromIndex = 0, toIndex = l - 1)
                        .map(TerminalNode::getText),
                type = ids[l - 1].text,
                genericsList = ctx.genericsBracket().typeIdentifier()
                        .map(this::visitTypeIdentifier)
        )
    }

}