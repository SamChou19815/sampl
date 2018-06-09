package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.StructTypeInDeclarationContext
import org.sampl.antlr.PLParser.VariantTypeInDeclarationContext
import java.util.stream.Collectors
import org.sampl.ast.type.TypeDeclaration as T

/**
 * [TypeExprInDeclarationBuilder] builds TypeExprInDeclaration AST from parse tree.
 */
internal object TypeExprInDeclarationBuilder : PLBaseVisitor<T>() {

    /**
     * [b] is the commonly used [TypeExprInAnnotationBuilder].
     */
    private val b = TypeExprInAnnotationBuilder

    override fun visitVariantTypeInDeclaration(ctx: VariantTypeInDeclarationContext): T =
            T.Variant(map = ctx.variantConstructorDeclaration().stream()
                    .collect(Collectors.toMap(
                            { it.UpperIdentifier().text },
                            { it.typeExprInAnnotation()?.accept(b) }
                    )))

    override fun visitStructTypeInDeclaration(ctx: StructTypeInDeclarationContext): T =
            T.Struct(map = ctx.annotatedVariable().stream().collect(Collectors.toMap(
                    { it.LowerIdentifier().text },
                    { it.typeAnnotation().typeExprInAnnotation().accept(b) }
            )))

}
