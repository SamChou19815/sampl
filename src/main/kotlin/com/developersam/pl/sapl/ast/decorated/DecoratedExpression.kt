package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.common.BinaryOperator
import com.developersam.pl.sapl.ast.common.Literal
import com.developersam.pl.sapl.ast.protocol.Printable
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.config.IndentationStrategy

/**
 * [DecoratedExpression] is an expression with a correct decorated type.
 *
 * @param isMultiline reports whether this expression will span multiple lines.
 */
sealed class DecoratedExpression(private val isMultiline: Boolean) : Printable {

    /**
     * [type] is the type decoration.
     */
    abstract val type: TypeExpr

    protected abstract fun exprPrettyPrint(level: Int, builder: StringBuilder)

    final override fun prettyPrint(level: Int, builder: StringBuilder) {
        exprPrettyPrint(level = level, builder = builder)
        if (!isMultiline) {
            builder.append('\n')
        }
    }

    final override fun prettyPrint(): String = super.prettyPrint()

}

/**
 * [DecoratedLiteralExpr] with correct [type] represents a [literal] as an expression.
 */
data class DecoratedLiteralExpr(
        val literal: Literal, override val type: TypeExpr
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        builder.append(literal.toString())
    }

}


/**
 * [DecoratedVariableIdentifierExpr] with correct [type] represents a [variable] identifier as an
 * expression.
 * It can only contain [genericInfo] which helps to determine the fixed type for this expression.
 */
data class DecoratedVariableIdentifierExpr(
        val variable: String, val genericInfo: List<TypeExpr>,
        override val type: TypeExpr
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        builder.append(variable)
    }

}

/**
 * [DecoratedConstructorExpr] with correct type represents a set of constructor expression defined
 * in type declarations.
 *
 * @param isMultiline reports whether this expression will span multiple lines.
 */
sealed class DecoratedConstructorExpr(isMultiline: Boolean) : DecoratedExpression(isMultiline) {

    /**
     * [NoArgVariant] with correct [type] represents a singleton value in variant with [typeName],
     * [variantName] and
     * some potential [genericInfo] to assist type inference.
     */
    data class NoArgVariant(
            val typeName: String, val variantName: String, val genericInfo: List<TypeExpr>,
            override val type: TypeExpr
    ) : DecoratedConstructorExpr(isMultiline = false) {

        override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
            builder.append(typeName).append('.').append(variantName)
            if (genericInfo.isNotEmpty()) {
                builder.append(genericInfo.joinToString(
                        separator = ", ", prefix = "<", postfix = ">"
                ))
            }
        }

    }

    /**
     * [OneArgVariant] with correct [type] represents a tagged enum in variant with [typeName],
     * [variantName] and associated [data].
     */
    data class OneArgVariant(
            val typeName: String, val variantName: String, val data: DecoratedExpression,
            override val type: TypeExpr
    ) : DecoratedConstructorExpr(isMultiline = false) {

        override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
            builder.append(typeName).append('.').append(variantName).append(" (")
            data.prettyPrint(level = level, builder = builder)
            builder.append(")")
        }

    }

    /**
     * [Struct] with correct [type] represents a struct initialization with [typeName] and initial
     * value [declarations].
     */
    data class Struct(
            val typeName: String, val declarations: Map<String, DecoratedExpression>,
            override val type: TypeExpr
    ) : DecoratedConstructorExpr(isMultiline = true) {

        override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
            IndentationStrategy.indent2(level, builder).append(typeName).append(" {\n")
            for ((name, expr) in declarations) {
                IndentationStrategy.indent2(level = level + 1, builder = builder)
                        .append(name)
                        .append(" = ")
                expr.prettyPrint(level = level + 1, builder = builder)
            }
            IndentationStrategy.indent2(level, builder).append("}\n")
        }

    }

    /**
     * [StructWithCopy] with correct [type] represents a copy of [old] struct with some new values
     * in [newDeclarations].
     */
    data class StructWithCopy(
            val old: DecoratedExpression, val newDeclarations: Map<String, DecoratedExpression>,
            override val type: TypeExpr
    ) : DecoratedConstructorExpr(isMultiline = true) {

        override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
            IndentationStrategy.indent2(level, builder).append(" {\n")
            old.prettyPrint(level = level + 1, builder = builder)
            builder.append("with\n")
            for ((name, expr) in newDeclarations) {
                IndentationStrategy.indent2(level = level + 1, builder = builder)
                        .append(name)
                        .append(" = ")
                expr.prettyPrint(level = level + 1, builder = builder)
            }
            IndentationStrategy.indent2(level, builder).append("}\n")
        }

    }

}

/**
 * [DecoratedStructMemberAccessExpr] with correct [type] represents accessing [memberName]
 * of [structExpr].
 */
data class DecoratedStructMemberAccessExpr(
        val structExpr: DecoratedExpression, val memberName: String, override val type: TypeExpr
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        builder.append('(')
        structExpr.prettyPrint(level = level, builder = builder)
        builder.append(").").append(memberName)
    }

}

/**
 * [DecoratedNotExpr] with correct [type] represents the logical inversion of expression [expr].
 */
data class DecoratedNotExpr(
        val expr: DecoratedExpression, override val type: TypeExpr
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        builder.append("!(")
        expr.prettyPrint(level = level, builder = builder)
        builder.append(")")
    }

}

/**
 * [DecoratedBinaryExpr] with correct [type] represents a binary expression with operator [op]
 * between [left] and [right].
 */
data class DecoratedBinaryExpr(
        val left: DecoratedExpression, val op: BinaryOperator, val right: DecoratedExpression,
        override val type: TypeExpr
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        left.prettyPrint(level = level, builder = builder)
        builder.append(' ').append(op.symbol).append(' ')
        right.prettyPrint(level = level, builder = builder)
    }

}

/**
 * [DecoratedThrowExpr] with correct [type] represents the throw exception expression, where the
 * thrown exception is [expr]. The throw expression is coerced to have [type].
 */
data class DecoratedThrowExpr(
        override val type: TypeExpr, val expr: DecoratedExpression
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        builder.append("throw<").append(type.toString()).append("> ")
        expr.prettyPrint(level = level, builder = builder)
    }

}

/**
 * [DecoratedIfElseExpr] with correct [type] represents the if else expression, guarded by
 * [condition] and having two branches [e1] and [e2].
 */
data class DecoratedIfElseExpr(
        val condition: DecoratedExpression, val e1: DecoratedExpression,
        val e2: DecoratedExpression, override val type: TypeExpr
) : DecoratedExpression(isMultiline = true) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder).append("if (")
        condition.prettyPrint(level = level, builder = builder)
        builder.append(") then (\n")
        e1.prettyPrint(level = level + 1, builder = builder)
        builder.append(") else (\n")
        e2.prettyPrint(level = level + 1, builder = builder)
        builder.append(")\n")
    }

}

/**
 * [DecoratedMatchExpr] with correct [type] represents the pattern matching expression, with a list
 * of [matchingList] to match against [exprToMatch].
 */
data class DecoratedMatchExpr(
        val exprToMatch: DecoratedExpression,
        val matchingList: List<Pair<DecoratedPattern, DecoratedExpression>>,
        override val type: TypeExpr
) : DecoratedExpression(isMultiline = true) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder).append("match (")
        exprToMatch.prettyPrint(level = level, builder = builder)
        builder.append(") with\n")
        for ((pattern, expr) in matchingList) {
            builder.append("| ").append(pattern.toString()).append(" ->\n")
            expr.prettyPrint(level = level + 1, builder = builder)
        }
    }

}

/**
 * [DecoratedLetExpr] with correct [type] represents the let expression of the form
 * `let` [identifier] `=` [e1] `;` [e2]
 */
data class DecoratedLetExpr(
        val identifier: String, val e1: DecoratedExpression, val e2: DecoratedExpression,
        override val type: TypeExpr
) : DecoratedExpression(isMultiline = true) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder).append("let ").append(identifier).append(" = ")
        e1.prettyPrint(level = level + 1, builder = builder)
        builder.append("in\n")
        e2.prettyPrint(level = level, builder = builder)
    }

}

/**
 * [DecoratedFunctionExpr] with correct [type] is the function expression with some [arguments],
 * a [returnType] and finally the function [body].
 */
data class DecoratedFunctionExpr(
        val arguments: List<Pair<String, TypeExpr>>,
        val returnType: TypeExpr, val body: DecoratedExpression,
        override val type: TypeExpr
) : DecoratedExpression(isMultiline = true) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder).append("function ")
        for ((name, t) in arguments) {
            builder.append('(').append(name).append(": ").append(t.toString()).append(") ")
        }
        builder.append("-> \n")
        body.prettyPrint(level = level + 1, builder = builder)
        IndentationStrategy.indent2(level, builder).append(")\n")
    }

}

/**
 * [DecoratedFunctionApplicationExpr] with correct [type] is the function application expression,
 * with [functionExpr] as the function and [arguments] as arguments of the function.
 */
data class DecoratedFunctionApplicationExpr(
        val functionExpr: DecoratedExpression, val arguments: List<DecoratedExpression>,
        override val type: TypeExpr
) : DecoratedExpression(isMultiline = false) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        builder.append('(')
        functionExpr.prettyPrint(level = level, builder = builder)
        builder.append(") (")
        for (argument in arguments) {
            argument.prettyPrint(level = level, builder = builder)
            builder.append(' ')
        }
        builder.append(")")
    }

}

/**
 * [DecoratedTryCatchExpr] with correct [type] represents the try catch finally structure as an
 * expression, where the [tryExpr] is evaluated, and guard by catch branch with [exception] in scope
 * and [catchHandler] to deal with it.
 */
data class DecoratedTryCatchExpr(
        val tryExpr: DecoratedExpression, val exception: String,
        val catchHandler: DecoratedExpression, override val type: TypeExpr
) : DecoratedExpression(isMultiline = true) {

    override fun exprPrettyPrint(level: Int, builder: StringBuilder) {
        IndentationStrategy.indent2(level, builder).append("try (")
        tryExpr.prettyPrint(level = level, builder = builder)
        builder.append(") catch (").append(exception).append(") (\n")
        catchHandler.prettyPrint(level = level + 1, builder = builder)
        builder.append(")\n")
    }

}
