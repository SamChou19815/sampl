package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.VariablePatternContext
import org.sampl.antlr.PLParser.VariantPatternContext
import org.sampl.antlr.PLParser.WildcardPatternContext
import org.sampl.ast.raw.Pattern

/**
 * [ExprBuilder] builds pattern AST from parse tree.
 */
internal object PatternBuilder : PLBaseVisitor<Pattern>() {

    override fun visitVariantPattern(ctx: VariantPatternContext): Pattern =
            Pattern.Variant(
                    lineNo = ctx.start.line,
                    variantIdentifier = ctx.UpperIdentifier().text,
                    associatedVariable = if (ctx.WILDCARD() != null) "_" else {
                        ctx.LowerIdentifier()?.text
                    }
            )

    override fun visitVariablePattern(ctx: VariablePatternContext): Pattern =
            Pattern.Variable(
                    lineNo = ctx.LowerIdentifier().symbol.line,
                    identifier = ctx.LowerIdentifier().text
            )

    override fun visitWildcardPattern(ctx: WildcardPatternContext): Pattern =
            Pattern.WildCard(lineNo = ctx.WILDCARD().symbol.line)

}