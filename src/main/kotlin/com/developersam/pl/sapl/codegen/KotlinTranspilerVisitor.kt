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
import com.developersam.pl.sapl.config.IndentationStrategy

/**
 * [KotlinTranspilerVisitor] is a [TranspilerVisitor] that transpiles the code to Kotlin source
 * code.
 */
class KotlinTranspilerVisitor : TranspilerVisitor {

    override val indentationStrategy: IndentationStrategy = IndentationStrategy.FOUR_SPACES

    override fun visit(q: IndentationQueue, program: DecoratedProgram) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, module: DecoratedModule) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, members: DecoratedModuleMembers) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, typeMember: ModuleTypeMember) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, constantMember: DecoratedModuleConstantMember) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, functionMember: DecoratedModuleFunctionMember) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, expression: DecoratedExpression) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, pattern: DecoratedPattern) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, typeDeclaration: TypeDeclaration) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, typeExpr: TypeExpr) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, typeIdentifier: TypeIdentifier) {
        TODO("not implemented")
    }

}
