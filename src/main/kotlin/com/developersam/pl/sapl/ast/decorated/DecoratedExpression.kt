package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.common.BinaryOperator
import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.util.joinToGenericsInfoString
import com.developersam.pl.sapl.ast.common.Literal as CommonLiteral

/**
 * [DecoratedExpression] is an expression with a correct decorated type.
 *
 * @param shouldBeInline reports whether the expression is intended to be an inline one.
 * @param precedenceLevel smaller this number, higher the precedence.
 */
sealed class DecoratedExpression(
        val shouldBeInline: Boolean, private val precedenceLevel: Int
) : Printable {

    /**
     * [type] is the type decoration.
     */
    abstract val type: TypeExpr

    final override val asIndentedSourceCode: String
        get() = if (shouldBeInline) {
            throw UnsupportedOperationException()
        } else {
            super.asIndentedSourceCode
        }

    /**
     * [hasLowerPrecedence] returns whether this expression has lower precedence than [parent].
     */
    fun hasLowerPrecedence(parent: DecoratedExpression): Boolean =
            if (this is Binary && parent is Binary) {
                op.precedenceLevel > parent.op.precedenceLevel
            } else {
                precedenceLevel > parent.precedenceLevel
            }

    /**
     * [addParenthesisIfNeeded] adds parenthesis if needed for code generation for expression.
     * It determines by comparing itself's precedence level with [parent].
     */
    private fun addParenthesisIfNeeded(parent: DecoratedExpression): String =
            asInlineSourceCode.let {
                if (hasLowerPrecedence(parent = parent)) "($it)" else it
            }

    override fun prettyPrint(q: IndentationQueue) {
        throw UnsupportedOperationException()
    }

    /**
     * [prettyPrintOrInline] tries to pretty print or to inline the expression onto [q] depends
     * on whether this expression [shouldBeInline].
     */
    fun prettyPrintOrInline(q: IndentationQueue) {
        if (shouldBeInline) {
            q.addLine(line = asInlineSourceCode)
        } else {
            prettyPrint(q = q)
        }
    }

    /**
     * [Literal] with correct [type] represents a [literal] as an expression.
     */
    data class Literal(
            val literal: CommonLiteral, override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 0) {

        override val asInlineSourceCode: String
            get() = literal.toString()

    }

    /**
     * [VariableIdentifier] with correct [type] represents a [variable] identifier as an
     * expression.
     * It can only contain [genericInfo] which helps to determine the fixed type for this
     * expression.
     */
    data class VariableIdentifier(
            val variable: String, val genericInfo: List<TypeExpr>,
            override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 1) {

        override val asInlineSourceCode: String
            get() = StringBuilder().apply {
                append(variable)
                if (genericInfo.isNotEmpty()) {
                    append(genericInfo.joinToGenericsInfoString())
                }
            }.toString()

    }

    /**
     * [Constructor] with correct type represents a set of constructor expression defined in type
     * declarations.
     *
     * @param shouldBeInline reports whether the expression is intended to be an inline one.
     */
    sealed class Constructor(shouldBeInline: Boolean) : DecoratedExpression(
            shouldBeInline = shouldBeInline, precedenceLevel = 2
    ) {


        /**
         * [NoArgVariant] with correct [type] represents a singleton value in variant with
         * [typeName], [variantName] and
         * some potential [genericInfo] to assist type inference.
         */
        data class NoArgVariant(
                val typeName: String, val variantName: String, val genericInfo: List<TypeExpr>,
                override val type: TypeExpr
        ) : Constructor(shouldBeInline = true) {

            override val asInlineSourceCode: String
                get() = StringBuilder().apply {
                    append(typeName).append('.').append(variantName)
                    if (genericInfo.isNotEmpty()) {
                        append(genericInfo.joinToGenericsInfoString())
                    }
                }.toString()

        }

        /**
         * [OneArgVariant] with correct [type] represents a tagged enum in variant with [typeName],
         * [variantName] and associated [data].
         */
        data class OneArgVariant(
                val typeName: String, val variantName: String, val data: DecoratedExpression,
                override val type: TypeExpr
        ) : Constructor(shouldBeInline = true) {

            override val asInlineSourceCode: String
                get() = StringBuilder().append(typeName)
                        .append('.').append(variantName)
                        .append(" (").append(data.asInlineSourceCode).append(")")
                        .toString()

        }

        /**
         * [Struct] with correct [type] represents a struct initialization with [typeName] and
         * initial value [declarations].
         */
        data class Struct(
                val typeName: String, val declarations: Map<String, DecoratedExpression>,
                override val type: TypeExpr
        ) : Constructor(shouldBeInline = false) {

            override fun prettyPrint(q: IndentationQueue) {
                q.addLine(line = "$typeName {")
                q.indentAndApply {
                    for ((name, expr) in declarations) {
                        addLine(line = "$name = ${expr.asInlineSourceCode};")
                    }
                }
                q.addLine(line = "}")
            }

        }

        /**
         * [StructWithCopy] with correct [type] represents a copy of [old] struct with some new
         * values in [newDeclarations].
         */
        data class StructWithCopy(
                val old: DecoratedExpression, val newDeclarations: Map<String, DecoratedExpression>,
                override val type: TypeExpr
        ) : Constructor(shouldBeInline = false) {

            override fun prettyPrint(q: IndentationQueue) {
                q.addLine(line = "{")
                val oldStructCode = old.addParenthesisIfNeeded(parent = this)
                q.indentAndApply {
                    addLine(line = "$oldStructCode with")
                    for ((name, expr) in newDeclarations) {
                        val exprCode = expr.addParenthesisIfNeeded(parent = this@StructWithCopy)
                        addLine(line = "$name = $exprCode")
                    }
                }
                q.addLine(line = "}")
            }

        }

    }

    /**
     * [StructMemberAccess] with correct [type] represents accessing [memberName]
     * of [structExpr].
     */
    data class StructMemberAccess(
            val structExpr: DecoratedExpression, val memberName: String, override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 3) {

        override val asInlineSourceCode: String
            get() {
                val structExprCode = structExpr.addParenthesisIfNeeded(parent = this)
                return "$structExprCode.$memberName"
            }

    }

    /**
     * [Not] with correct [type] represents the logical inversion of expression [expr].
     */
    data class Not(
            val expr: DecoratedExpression, override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 4) {

        override val asInlineSourceCode: String
            get() = "!${expr.addParenthesisIfNeeded(parent = this)}"

    }

    /**
     * [Binary] with correct [type] represents a binary expression with operator [op]
     * between [left] and [right].
     */
    data class Binary(
            val left: DecoratedExpression, val op: BinaryOperator, val right: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 5) {

        override val asInlineSourceCode: String
            get() {
                val leftCode = left.addParenthesisIfNeeded(parent = this)
                val rightCode = left.addParenthesisIfNeeded(parent = this)
                return "$leftCode ${op.symbol} $rightCode"
            }

    }

    /**
     * [Throw] with correct [type] represents the throw exception expression, where the
     * thrown exception is [expr]. The throw expression is coerced to have [type].
     */
    data class Throw(
            override val type: TypeExpr, val expr: DecoratedExpression
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 6) {

        override val asInlineSourceCode: String
            get() = "throw<$type> ${expr.addParenthesisIfNeeded(parent = this)}"

    }

    /**
     * [IfElse] with correct [type] represents the if else expression, guarded by [condition] and
     * having two branches [e1] and [e2].
     */
    data class IfElse(
            val condition: DecoratedExpression, val e1: DecoratedExpression,
            val e2: DecoratedExpression, override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = false, precedenceLevel = 7) {

        override fun prettyPrint(q: IndentationQueue) {
            q.addLine(line = "if (${condition.asInlineSourceCode}) then (")
            q.indentAndApply { e1.prettyPrintOrInline(q = this) }
            q.addLine(line = ") else (")
            q.indentAndApply { e2.prettyPrintOrInline(q = this) }
            q.addLine(line = ")")
        }

    }

    /**
     * [Match] with correct [type] represents the pattern matching expression, with a list
     * of [matchingList] to match against [exprToMatch].
     */
    data class Match(
            val exprToMatch: DecoratedExpression,
            val matchingList: List<Pair<DecoratedPattern, DecoratedExpression>>,
            override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = false, precedenceLevel = 8) {

        override fun prettyPrint(q: IndentationQueue) {
            val matchedCode = exprToMatch.addParenthesisIfNeeded(parent = this)
            q.addLine(line = "match $matchedCode with")
            for ((pattern, expr) in matchingList) {
                val lineCommon = "| ${pattern.asSourceCode} ->"
                val action: IndentationQueue.() -> Unit = { expr.prettyPrintOrInline(q = this) }
                if (expr.hasLowerPrecedence(parent = this)) {
                    q.addLine(line = "$lineCommon (")
                    q.indentAndApply(action = action)
                    q.addLine(line = ")")
                } else {
                    q.addLine(line = lineCommon)
                    q.indentAndApply(action = action)
                }
            }
        }

    }

    /**
     * [FunctionApplication] with correct [type] is the function application expression,
     * with [functionExpr] as the function and [arguments] as arguments of the function.
     */
    data class FunctionApplication(
            val functionExpr: DecoratedExpression, val arguments: List<DecoratedExpression>,
            override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = true, precedenceLevel = 9) {

        override val asInlineSourceCode: String
            get() {
                val functionCode = functionExpr.addParenthesisIfNeeded(parent = this)
                val argumentCode = arguments.joinToString(
                        separator = " ", prefix = "(", postfix = ")"
                ) { it.asInlineSourceCode }
                return "$functionCode $argumentCode"
            }
    }

    /**
     * [Function] with correct [type] is the function expression with some [arguments], a
     * [returnType] and finally the function [body].
     */
    data class Function(
            val arguments: List<Pair<String, TypeExpr>>,
            val returnType: TypeExpr, val body: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = false, precedenceLevel = 10) {

        override fun prettyPrint(q: IndentationQueue) {
            val header = StringBuilder().append("function ").apply {
                for ((name, t) in arguments) {
                    append('(').append(name).append(": ").append(t.toString()).append(") ")
                }
            }.append("-> (").toString()
            q.addLine(line = header)
            q.indentAndApply { body.prettyPrintOrInline(q = this) }
            q.addLine(line = ")")
        }

    }

    /**
     * [TryCatch] with correct [type] represents the try catch finally structure as an
     * expression, where the [tryExpr] is evaluated, and guard by catch branch with [exception] in
     * scope and [catchHandler] to deal with it.
     */
    data class TryCatch(
            val tryExpr: DecoratedExpression, val exception: String,
            val catchHandler: DecoratedExpression, override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = false, precedenceLevel = 11) {

        override fun prettyPrint(q: IndentationQueue) {
            if (tryExpr.shouldBeInline) {
                q.addLine(line = "try (${tryExpr.asInlineSourceCode})")
            } else {
                q.addLine(line = "try (")
                q.indentAndApply { tryExpr.prettyPrint(q = this) }
                q.addLine(line = ")")
            }
            if (catchHandler.shouldBeInline) {
                q.addLine(line = "catch $exception (${catchHandler.asInlineSourceCode})")
            } else {
                q.addLine(line = "catch $exception (")
                q.indentAndApply { catchHandler.prettyPrint(q = this) }
                q.addLine(line = ")")
            }
        }

    }

    /**
     * [Let] with correct [type] represents the let expression of the form
     * `let` [identifier] `=` [e1] `;` [e2]
     */
    data class Let(
            val identifier: String, val e1: DecoratedExpression, val e2: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedExpression(shouldBeInline = false, precedenceLevel = 12) {

        override fun prettyPrint(q: IndentationQueue) {
            q.addLine(line = "let $identifier = ${e1.addParenthesisIfNeeded(parent = this)};")
            e2.prettyPrintOrInline(q = q)
        }

    }

}
