package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.*
import com.developersam.pl.sapl.ast.Pattern
import com.developersam.pl.sapl.ast.UnitPattern
import com.developersam.pl.sapl.ast.VariablePattern
import com.developersam.pl.sapl.ast.VariantPattern
import com.developersam.pl.sapl.ast.WildCardPattern

/**
 * [ExprBuilder] builds pattern AST from parse tree.
 */
object PatternBuilder : PLBaseVisitor<Pattern>() {

    override fun visitUnitPattern(ctx: UnitPatternContext): Pattern = UnitPattern

    override fun visitVariantPattern(ctx: VariantPatternContext): Pattern =
            VariantPattern(
                    variantIdentifier = ctx.UpperIdentifier().text,
                    associatedVariable = ctx.LowerIdentifier()?.text
            )

    override fun visitVariablePattern(ctx: VariablePatternContext): Pattern =
            VariablePattern(identifier = ctx.LowerIdentifier().text)

    override fun visitWildcardPattern(ctx: WildcardPatternContext): Pattern = WildCardPattern

}