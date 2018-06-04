package com.developersam.pl.sapl.codegen

import com.developersam.pl.sapl.ast.decorated.DecoratedExpression
import com.developersam.pl.sapl.ast.decorated.DecoratedModule
import com.developersam.pl.sapl.ast.decorated.DecoratedModuleConstantMember
import com.developersam.pl.sapl.ast.decorated.DecoratedModuleFunctionMember
import com.developersam.pl.sapl.ast.decorated.DecoratedModuleMembers
import com.developersam.pl.sapl.ast.decorated.DecoratedPattern
import com.developersam.pl.sapl.ast.decorated.DecoratedProgram
import com.developersam.pl.sapl.ast.raw.ModuleTypeMember
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.codegen.IndentationQueue
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
     * [visit] visits the [module] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, module: DecoratedModule)

    /**
     * [visit] visits the [members] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, members: DecoratedModuleMembers)

    /**
     * [visit] visits the [typeMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, typeMember: ModuleTypeMember)

    /**
     * [visit] visits the [constantMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, constantMember: DecoratedModuleConstantMember)

    /**
     * [visit] visits the [functionMember] with [q] to add proper indentation and code info.
     */
    fun visit(q: IndentationQueue, functionMember: DecoratedModuleFunctionMember)

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
