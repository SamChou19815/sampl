package org.sampl.parser

import org.sampl.antlr.PLBaseVisitor
import org.sampl.antlr.PLParser.NoArgVariantConstructorContext
import org.sampl.antlr.PLParser.OneArgVariantConstructorContext
import org.sampl.antlr.PLParser.StructConstructorContext
import org.sampl.antlr.PLParser.StructWithConstructorContext
import org.sampl.ast.raw.Expression
import org.sampl.antlr.PLParser.StructConstructorValueDeclarationContext as CVC
import org.sampl.ast.raw.ConstructorExpr as E

/**
 * [ExprBuilder] builds constructor expression AST from parse tree.
 */
object ConstructorExprBuilder : PLBaseVisitor<E>() {

    override fun visitNoArgVariantConstructor(ctx: NoArgVariantConstructorContext): E {
        val ids = ctx.UpperIdentifier()
        val typeName = ids.subList(fromIndex = 0, toIndex = ids.size - 1)
                .joinToString(separator = ".")
        val variantName = ids[ids.size - 1].text
        val genericInfo = ctx.genericsSpecialization()?.typeExprInAnnotation()
                ?.map { it.accept(TypeExprInAnnotationBuilder) } ?: emptyList()
        return E.NoArgVariant(
                typeName = typeName, variantName = variantName, genericInfo = genericInfo
        )
    }

    override fun visitOneArgVariantConstructor(ctx: OneArgVariantConstructorContext): E {
        val ids = ctx.UpperIdentifier()
        val typeName = ctx.UpperIdentifier()
                .subList(fromIndex = 0, toIndex = ids.size - 1)
                .joinToString(separator = ".")
        val variantName = ids[ids.size - 1].text
        val data = ctx.expression().accept(ExprBuilder)
        return E.OneArgVariant(typeName = typeName, variantName = variantName, data = data)
    }

    /**
     * [buildDeclaration] builds the [ctx] to a pair that maps identifiers to expressions.
     */
    private fun buildDeclaration(ctx: CVC): Pair<String, Expression> =
            ctx.LowerIdentifier().text to ctx.expression().accept(ExprBuilder)

    override fun visitStructConstructor(ctx: StructConstructorContext): E =
            E.Struct(
                    typeName = ctx.UpperIdentifier().joinToString(separator = "."),
                    declarations = ctx.structConstructorValueDeclaration()
                            .map(::buildDeclaration).toMap()
            )

    override fun visitStructWithConstructor(ctx: StructWithConstructorContext): E =
            E.StructWithCopy(
                    old = ctx.expression().accept(ExprBuilder),
                    newDeclarations = ctx.structConstructorValueDeclaration()
                            .map(::buildDeclaration).toMap()
            )

}
