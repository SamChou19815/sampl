package com.developersam.pl.sapl.parser

import java.util.stream.Collectors

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.StructTypeInDeclarationContext
import com.developersam.pl.sapl.antlr.PLParser.VariantTypeInDeclarationContext
import com.developersam.pl.sapl.ast.StructTypeInDeclaration
import com.developersam.pl.sapl.ast.VariantTypeInDeclaration
import com.developersam.pl.sapl.ast.TypeExprInDeclaration as T
import com.developersam.pl.sapl.parser.TypeExprInAnnotationBuilder as B

/**
 * [TypeExprInDeclarationBuilder] builds TypeExprInDeclaration AST from parse tree.
 */
object TypeExprInDeclarationBuilder : PLBaseVisitor<T>() {

    override fun visitVariantTypeInDeclaration(ctx: VariantTypeInDeclarationContext): T =
            VariantTypeInDeclaration(map = ctx.variantConstructorDeclaration().stream()
                    .collect(Collectors.toMap(
                            { it.UpperIdentifier().text },
                            { it.typeExprInAnnotation()?.accept(B) }
                    )))

    override fun visitStructTypeInDeclaration(ctx: StructTypeInDeclarationContext): T =
            StructTypeInDeclaration(
                    map = ctx.annotatedVariable().stream().collect(Collectors.toMap(
                            { it.LowerIdentifier().text },
                            { it.typeAnnotation().typeExprInAnnotation().accept(B) }
                    ))
            )

}
