package org.sampl.codegen

import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassFunctionMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeIdentifier

/**
 * [AstToCodeConverter] defines a set of methods that helps the conversion from AST to target code.
 * This interface is designed to be target-code independent, so it can be used both for pretty
 * print and compilation.
 */
interface AstToCodeConverter {

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedProgram)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedClass)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedClassMembers)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedClassConstantMember)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedClassFunctionMember)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Literal)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.VariableIdentifier)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Constructor)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.StructMemberAccess)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Not)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Binary)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Throw)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.IfElse)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Match)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.FunctionApplication)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Function)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.TryCatch)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Let)


}
