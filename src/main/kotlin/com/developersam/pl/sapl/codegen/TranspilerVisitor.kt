package com.developersam.pl.sapl.codegen

import com.developersam.pl.sapl.ast.decorated.DecoratedExpression
import com.developersam.pl.sapl.ast.decorated.DecoratedClass
import com.developersam.pl.sapl.ast.decorated.DecoratedClassConstantMember
import com.developersam.pl.sapl.ast.decorated.DecoratedClassFunctionMember
import com.developersam.pl.sapl.ast.decorated.DecoratedClassMembers
import com.developersam.pl.sapl.ast.decorated.DecoratedPattern
import com.developersam.pl.sapl.ast.decorated.DecoratedProgram
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.config.IndentationStrategy

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
    fun visit(q: IndentationQueue, expression: DecoratedExpression)

    /**
     * [visit] visits the [pattern] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, pattern: DecoratedPattern)

    /**
     * [visit] visits the [typeDeclaration] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, typeDeclaration: TypeDeclaration)

    /**
     * [visit] visits the [typeExpr] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, typeExpr: TypeExpr)

    /**
     * [visit] visits the [typeIdentifier] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, typeIdentifier: TypeIdentifier)

}
