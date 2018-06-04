package com.developersam.pl.sapl.codegen

import com.developersam.pl.sapl.TOP_LEVEL_MODULE_NAME
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
import com.developersam.pl.sapl.ast.type.unitTypeExpr
import com.developersam.pl.sapl.config.IndentationStrategy

/**
 * [KotlinTranspilerVisitor] is a [TranspilerVisitor] that transpiles the code to Kotlin source
 * code.
 */
class KotlinTranspilerVisitor : TranspilerVisitor {

    override val indentationStrategy: IndentationStrategy = IndentationStrategy.FOUR_SPACES

    override fun visit(q: IndentationQueue, program: DecoratedProgram) {
        q.addLine(line = """@file:JvmName(name = "$TOP_LEVEL_MODULE_NAME")""")
        q.addEmptyLine()
        val clazz = program.clazz
        visit(q = q, clazz = clazz)
        clazz.members.functionMembers.firstOrNull { member ->
            member.isPublic && member.identifier == "main" && member.arguments.size == 1
            && member.arguments[0] == "_unit_" to unitTypeExpr && member.returnType == unitTypeExpr
        } ?: return
        q.addEmptyLine()
        q.addLine(line = "fun main(args: Array<String>) {")
        q.indentAndApply {
            addLine(line = "${clazz.identifier.name}.main(_unit_ = Unit)")
        }
        q.addLine(line = "}")
    }

    override fun visit(q: IndentationQueue, clazz: DecoratedClass) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, members: DecoratedClassMembers) {
        members.constantMembers.forEach { visit(q = q, constantMember = it) }
        members.functionMembers.forEach { visit(q = q, functionMember = it) }
        members.nestedClassMembers.forEach { visit(q = q, clazz = it) }
    }

    override fun visit(q: IndentationQueue, constantMember: DecoratedClassConstantMember) {
        TODO("not implemented")
    }

    override fun visit(q: IndentationQueue, functionMember: DecoratedClassFunctionMember) {
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
