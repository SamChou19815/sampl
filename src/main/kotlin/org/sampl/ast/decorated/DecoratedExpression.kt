package org.sampl.ast.decorated

import org.sampl.ast.common.BinaryOperator
import org.sampl.ast.type.TypeExpr
import org.sampl.codegen.AstToCodeConverter
import org.sampl.codegen.CodeConvertible
import org.sampl.ast.common.Literal as CommonLiteral

/**
 * [DecoratedExpression] is an expression with a correct decorated type.
 *
 * @param precedenceLevel smaller this number, higher the precedence.
 */
sealed class DecoratedExpression(private val precedenceLevel: Int) : CodeConvertible {

    /**
     * [type] is the type decoration.
     */
    abstract val type: TypeExpr

    /**
     * [hasLowerPrecedence] returns whether this expression has lower precedence than [parent].
     */
    fun hasLowerPrecedence(parent: DecoratedExpression): Boolean =
            if (this is Binary && parent is Binary) {
                op.precedenceLevel >= parent.op.precedenceLevel
            } else {
                precedenceLevel > parent.precedenceLevel
            }

    /**
     * [Dummy] represents a dummy decorated expression used as a placeholder.
     * It should be used with primitive and provided runtime functions.
     */
    object Dummy : DecoratedExpression(precedenceLevel = -1) {

        override val type: TypeExpr get() = throw UnsupportedOperationException()

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter) {
            throw UnsupportedOperationException()
        }

    }

    /**
     * [Literal] with correct [type] represents a [literal] as an expression.
     *
     * @property literal the literal object.
     */
    data class Literal(
            val literal: CommonLiteral, override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 0) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [VariableIdentifier] with correct [type] represents a [variable] identifier as an
     * expression.
     * It can only contain [genericInfo] which helps to determine the fixed type for this
     * expression.
     *
     * @property variable the variable to refer to.
     * @property genericInfo a list of associated generics info, if any.
     * @property isClassFunction whether it's referring to a class function.
     */
    data class VariableIdentifier(
            val variable: String, val genericInfo: List<TypeExpr>,
            val isClassFunction: Boolean, override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 1) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Constructor] with correct type represents a set of constructor expression defined in type
     * declarations.
     */
    sealed class Constructor : DecoratedExpression(precedenceLevel = 2) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

        /**
         * [NoArgVariant] with correct [type] represents a singleton value in variant with
         * [typeName], [variantName] and
         * some potential [genericsInfo] to assist type inference.
         *
         * @property typeName the name of the type.
         * @property variantName the name of the variant.
         * @property genericsInfo a list of associated generics info.
         */
        data class NoArgVariant(
                val typeName: String, val variantName: String, val genericsInfo: List<TypeExpr>,
                override val type: TypeExpr
        ) : Constructor()

        /**
         * [OneArgVariant] with correct [type] represents a tagged enum in variant with [typeName],
         * [variantName] and associated [data].
         *
         * @property typeName the name of the type.
         * @property variantName the name of the variant.
         * @property data the data bind to the variant.
         */
        data class OneArgVariant(
                val typeName: String, val variantName: String, val data: DecoratedExpression,
                override val type: TypeExpr
        ) : Constructor()

        /**
         * [Struct] with correct [type] represents a struct initialization with [typeName] and
         * initial value [declarations].
         *
         * @property typeName the name of the type.
         * @property declarations the declaration map of the struct.
         */
        data class Struct(
                val typeName: String, val declarations: Map<String, DecoratedExpression>,
                override val type: TypeExpr
        ) : Constructor()

        /**
         * [StructWithCopy] with correct [type] represents a copy of [old] struct with some new
         * values in [newDeclarations].
         *
         * @property old the old source struct.
         * @property newDeclarations a map of new declarations.
         */
        data class StructWithCopy(
                val old: DecoratedExpression, val newDeclarations: Map<String, DecoratedExpression>,
                override val type: TypeExpr
        ) : Constructor()

    }

    /**
     * [StructMemberAccess] with correct [type] represents accessing [memberName]
     * of [structExpr].
     *
     * @property structExpr the expression for the struct.
     * @property memberName the name of the member.
     */
    data class StructMemberAccess(
            val structExpr: DecoratedExpression, val memberName: String, override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 3) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Not] with correct [type] represents the logical inversion of expression [expr].
     *
     * @property expr the expression to invert.
     */
    data class Not(
            val expr: DecoratedExpression, override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 4) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Binary] with correct [type] represents a binary expression with operator [op]
     * between [left] and [right].
     *
     * @property left left part.
     * @property op the operator.
     * @property right right part.
     */
    data class Binary(
            val left: DecoratedExpression, val op: BinaryOperator, val right: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 5) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Throw] with correct [type] represents the throw exception expression, where the
     * thrown exception is [expr]. The throw expression is coerced to have [type].
     *
     * @property expr the stuff to throw.
     */
    data class Throw(
            override val type: TypeExpr, val expr: DecoratedExpression
    ) : DecoratedExpression(precedenceLevel = 6) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [IfElse] with correct [type] represents the if else expression, guarded by [condition] and
     * having two branches [e1] and [e2].
     *
     * @property condition the condition to check.
     * @property e1 expression of the first branch.
     * @property e2 expression of the second branch.
     */
    data class IfElse(
            val condition: DecoratedExpression, val e1: DecoratedExpression,
            val e2: DecoratedExpression, override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 7) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Match] with correct [type] represents the pattern matching expression, with a list
     * of [matchingList] to match against [exprToMatch].
     *
     * @property exprToMatch the expression to match.
     * @property matchingList a list of functions to match the pattern.
     */
    data class Match(
            val exprToMatch: DecoratedExpression,
            val matchingList: List<Pair<DecoratedPattern, DecoratedExpression>>,
            override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 8) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [FunctionApplication] with correct [type] is the function application expression,
     * with [functionExpr] as the function and [arguments] as arguments of the function.
     *
     * @property functionExpr the function expression to apply.
     * @property arguments arguments to supply.
     */
    data class FunctionApplication(
            val functionExpr: DecoratedExpression, val arguments: List<DecoratedExpression>,
            override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 9) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Function] with correct [type] is the function expression with some [arguments], a
     * [returnType] and finally the function [body].
     */
    data class Function(
            val arguments: List<Pair<String, TypeExpr>>,
            val returnType: TypeExpr, val body: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 10) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [TryCatch] with correct [type] represents the try catch finally structure as an
     * expression, where the [tryExpr] is evaluated, and guard by catch branch with [exception] in
     * scope and [catchHandler] to deal with it.
     */
    data class TryCatch(
            val tryExpr: DecoratedExpression, val exception: String,
            val catchHandler: DecoratedExpression, override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 11) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

    /**
     * [Let] with correct [type] represents the let expression of the form
     * `let` [identifier] `=` [e1] `;` [e2]
     * If [identifier] is `null`, it means it's a wildcard.
     *
     * @property identifier new identifier to name.
     * @property e1 the expression for the identifier.
     * @property e2 the expression after the let.
     * @property type type of the let expression.
     */
    data class Let(
            val identifier: String?, val e1: DecoratedExpression, val e2: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedExpression(precedenceLevel = 12) {

        /**
         * @see CodeConvertible.acceptConversion
         */
        override fun acceptConversion(converter: AstToCodeConverter): Unit =
                converter.convert(node = this)

    }

}
