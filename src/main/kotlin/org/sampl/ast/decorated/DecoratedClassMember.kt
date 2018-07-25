package org.sampl.ast.decorated

import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeIdentifier
import org.sampl.codegen.AstToCodeConverter
import org.sampl.codegen.CodeConvertible

/**
 * [DecoratedClassMember] contains a set of class members with type decorations.
 */
internal sealed class DecoratedClassMember : CodeConvertible {

    /**
     * [Constant] represents a constant declaration of the form:
     * `public/private`([isPublic]) `let` [identifier] `=` [expr].
     * It has an additional [type] field.
     *
     * @property isPublic whether the constant is public.
     * @property identifier identifier of the constant.
     * @property expr expression of the constant.
     * @property type type of the constant.
     */
    data class Constant(
            val isPublic: Boolean, val identifier: String,
            val expr: DecoratedExpression, val type: TypeExpr
    ) : DecoratedClassMember() {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [FunctionGroup] represents a group of functions, where each function has type annotation.
     *
     * @property functions a group of functions.
     */
    data class FunctionGroup(
            val functions: List<DecoratedClassFunction>
    ) : DecoratedClassMember() {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Clazz] node has an type identifier with generics [identifier], a type [declaration]
     * and a set of ordered [members].
     * It contains decorated ASTs.
     *
     * @property identifier identifier of the class.
     * @property declaration the type declaration.
     * @property members a list of members of the class.
     */
    data class Clazz(
            val identifier: TypeIdentifier,
            val declaration: TypeDeclaration,
            val members: List<DecoratedClassMember>
    ) : DecoratedClassMember() {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }


}