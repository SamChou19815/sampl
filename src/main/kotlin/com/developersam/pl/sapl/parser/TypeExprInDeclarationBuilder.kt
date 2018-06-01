package com.developersam.pl.sapl.parser

import com.developersam.pl.sapl.antlr.PLBaseVisitor
import com.developersam.pl.sapl.antlr.PLParser.StructTypeInDeclarationContext
import com.developersam.pl.sapl.antlr.PLParser.VariantTypeInDeclarationContext
import java.util.stream.Collectors
import com.developersam.pl.sapl.ast.TypeDeclaration as T
import com.developersam.pl.sapl.parser.TypeExprInAnnotationBuilder as B

/**
 * [TypeExprInDeclarationBuilder] builds TypeExprInDeclaration AST from parse tree.
 */
internal object TypeExprInDeclarationBuilder : PLBaseVisitor<T>() {

    override fun visitVariantTypeInDeclaration(ctx: VariantTypeInDeclarationContext): T =
            T.Variant(map = ctx.variantConstructorDeclaration().stream()
                    .collect(Collectors.toMap(
                            { it.UpperIdentifier().text },
                            { it.typeExprInAnnotation()?.accept(B) }
                    )))

    override fun visitStructTypeInDeclaration(ctx: StructTypeInDeclarationContext): T =
            T.Struct(map = ctx.annotatedVariable().stream().collect(Collectors.toMap(
                    { it.LowerIdentifier().text },
                    { it.typeAnnotation().typeExprInAnnotation().accept(B) }
            )))

}
