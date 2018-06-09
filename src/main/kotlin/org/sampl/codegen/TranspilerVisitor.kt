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
    val indentationStrategy: IdtStrategy

    /**
     * [visit] visits the [program] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, program: DecoratedProgram)

    /**
     * [visit] visits the [clazz] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, clazz: DecoratedClass)

    /**
     * [visit] visits the [members] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, members: DecoratedClassMembers)

    /**
     * [visit] visits the [constantMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, constantMember: DecoratedClassConstantMember)

    /**
     * [visit] visits the [functionMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, functionMember: DecoratedClassFunctionMember)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Literal)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.VariableIdentifier)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Constructor)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.StructMemberAccess)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Not)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Binary)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Throw)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.IfElse)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Match)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.FunctionApplication)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Function)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.TryCatch)

    /**
     * [visit] visits the [expression] with [q] to add proper indentation and code info.
     */
    fun visit(q: IdtQueue, expression: DecoratedExpression.Let)


}
