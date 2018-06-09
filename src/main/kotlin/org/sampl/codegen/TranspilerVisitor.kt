package org.sampl.codegen

import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassFunctionMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedProgram

/**
 * [TranspilerVisitor] defines a series of related visitor methods for an transpiler visitor to
 * visit the AST nodes exhaustively.
 * This design of the visitor patterns allows the flexible implementation of variable transpiling
 * targets.
 */
interface TranspilerVisitor {

    /**
     * [indentationStrategy] is the indentation strategy used when finally generating the code.
     */
    val indentationStrategy: IndentationStrategy

    /**
     * [visit] visits the [program] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, program: DecoratedProgram)

    /**
     * [visit] visits the [clazz] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, clazz: DecoratedClass)

    /**
     * [visit] visits the [members] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, members: DecoratedClassMembers)

    /**
     * [visit] visits the [constantMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, constantMember: DecoratedClassConstantMember)

    /**
     * [visit] visits the [functionMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, functionMember: DecoratedClassFunctionMember)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Literal)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.VariableIdentifier)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Constructor)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.StructMemberAccess)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Not)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Binary)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Throw)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.IfElse)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Match)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.FunctionApplication)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Function)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.TryCatch)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, expression: DecoratedExpression.Let)


}
