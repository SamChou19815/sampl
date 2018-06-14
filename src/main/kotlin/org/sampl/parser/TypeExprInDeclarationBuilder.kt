package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.StructTypeInDeclarationContext
import org.sampl.antlr.PLParser.VariantTypeInDeclarationContext
import org.sampl.ast.type.TypeDeclaration.Struct
import org.sampl.ast.type.TypeDeclaration.Variant
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
            ctx.variantConstructorDeclaration().asSequence()
                .map { it.UpperIdentifier().text to it.typeExprInAnnotation()?.accept(b) }
                .toMap()
                .let { Variant(map = it) }

    override fun visitStructTypeInDeclaration(ctx: StructTypeInDeclarationContext): T =
            ctx.annotatedVariable().asSequence()
                    .map { pair ->
                        val name = pair.LowerIdentifier().text
                        val typeExpr = pair.typeAnnotation().typeExprInAnnotation().accept(b)
                        name to typeExpr
                    }
                    .toMap()
                    .let { Struct(map = it) }

}
