package org.sampl.codegen

import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassFunctionMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedExpression.Binary
import org.sampl.ast.decorated.DecoratedExpression.Constructor
import org.sampl.ast.decorated.DecoratedExpression.FunctionApplication
import org.sampl.ast.decorated.DecoratedExpression.IfElse
import org.sampl.ast.decorated.DecoratedExpression.Let
import org.sampl.ast.decorated.DecoratedExpression.Literal
import org.sampl.ast.decorated.DecoratedExpression.Match
import org.sampl.ast.decorated.DecoratedExpression.Not
import org.sampl.ast.decorated.DecoratedExpression.StructMemberAccess
import org.sampl.ast.decorated.DecoratedExpression.Throw
import org.sampl.ast.decorated.DecoratedExpression.TryCatch
import org.sampl.ast.decorated.DecoratedExpression.VariableIdentifier
import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeIdentifier

/**
 * [AstToCodeConverter] defines a set of methods that helps the conversion from AST to target code.
 * This interface is designed to be target-code independent, so it can be used both for pretty
 * print and compilation.
 */
interface AstToCodeConverter {

    // Part 1: Methods for defining default behaviors.

    /**
     * [createEmptyIndentationQueue] returns a newly created empty [IdtQueue].
     * This function is used for local code generation, such as one-liner code generation.
     */
    fun createEmptyIndentationQueue(): IdtQueue

    /**
     * [convert] converts the top-level [program] to well-indented code and returns the code.
     *
     * The default implementation assumes that the [program] is a suitable top-level node.
     * Generally, this method does not need to be overridden.
     */
    fun convertProgramToCode(program: DecoratedProgram): String =
            createEmptyIndentationQueue()
                    .apply { convert(node = program) }
                    .toIndentedCode()

    // Part 2: Visitor methods.

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
    fun convert(node: TypeExpr)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: TypeIdentifier)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Literal)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: VariableIdentifier)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Constructor)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: StructMemberAccess)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Not)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Binary)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Throw)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: IfElse)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Match)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: FunctionApplication)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: DecoratedExpression.Function)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: TryCatch)

    /**
     * [convert] converts the given [node] to target code by recording well-indented code info.
     */
    fun convert(node: Let)


}
