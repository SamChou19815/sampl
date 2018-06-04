package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.ArgumentDeclarationsContext as C
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.ast.type.unitTypeExpr

/**
 * [ArgumentDeclarationsBuilder] builds argument declarations into AST.
 */
object ArgumentDeclarationsBuilder : PLBaseVisitor<List<Pair<String, TypeExpr>>>() {

    override fun visitArgumentDeclarations(ctx: C): List<Pair<String, TypeExpr>> {
        val argumentDeclarations: ArrayList<Pair<String, TypeExpr>> = ArrayList(
                ctx.argumentDeclaration().map { it.accept(ArgumentDeclarationBuilder) }
        )
        val last = ctx.lastArgumentDeclaration()
        if (last.argumentDeclaration() == null) {
            /*
             * An illegal type identifier that is impossible to be used by the legal program, since
             * it cannot pass the parser parse. Therefore, this can be used to desugar the unit
             * argument expression.
             */
            argumentDeclarations.add("_unit_" to unitTypeExpr)
        } else {
            argumentDeclarations.add(last.argumentDeclaration().accept(ArgumentDeclarationBuilder))
        }
        return argumentDeclarations
    }

}