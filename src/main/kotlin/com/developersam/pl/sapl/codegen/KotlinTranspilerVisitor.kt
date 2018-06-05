package com.developersam.pl.sapl.codegen

import com.developersam.pl.sapl.TOP_LEVEL_PROGRAM_NAME
import com.developersam.pl.sapl.ast.common.Literal
import com.developersam.pl.sapl.ast.decorated.DecoratedClass
import com.developersam.pl.sapl.ast.decorated.DecoratedClassConstantMember
import com.developersam.pl.sapl.ast.decorated.DecoratedClassFunctionMember
import com.developersam.pl.sapl.ast.decorated.DecoratedClassMembers
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.Binary
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.Constructor
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.FunctionApplication
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.IfElse
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.Let
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.Match
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.Not
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.StructMemberAccess
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.Throw
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.TryCatch
import com.developersam.pl.sapl.ast.decorated.DecoratedExpression.VariableIdentifier
import com.developersam.pl.sapl.ast.decorated.DecoratedPattern
import com.developersam.pl.sapl.ast.decorated.DecoratedProgram
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeExpr
import com.developersam.pl.sapl.ast.type.TypeIdentifier
import com.developersam.pl.sapl.ast.type.unitTypeExpr
import com.developersam.pl.sapl.config.IndentationStrategy
import com.developersam.pl.sapl.util.joinToGenericsInfoString

/**
 * [KotlinTranspilerVisitor] is a [TranspilerVisitor] that transpiles the code to Kotlin source
 * code.
 */
class KotlinTranspilerVisitor : TranspilerVisitor {

    override val indentationStrategy: IndentationStrategy = IndentationStrategy.FOUR_SPACES

    override fun visit(q: IndentationQueue, program: DecoratedProgram) {
        q.addLine(line = """@file:JvmName(name = "$TOP_LEVEL_PROGRAM_NAME")""")
        q.addEmptyLine()
        q.addLine(line = "class PLException(val m: String): RuntimeException(m)")
        q.addEmptyLine()
        val clazz = program.clazz
        visit(q = q, clazz = clazz)
        clazz.members.functionMembers.firstOrNull { member ->
            member.isPublic && member.identifier == "main" && member.arguments.size == 1
                    && member.arguments[0] == "_unit_" to unitTypeExpr
                    && member.returnType == unitTypeExpr
        } ?: return
        q.addEmptyLine()
        q.addLine(line = "fun main(args: Array<String>) {")
        q.indentAndApply {
            addLine(line = "${clazz.identifier.name}.main(_unit_ = Unit)")
        }
        q.addLine(line = "}")
    }

    override fun visit(q: IndentationQueue, clazz: DecoratedClass) {
        val dec = clazz.declaration
        when (dec) {
            is TypeDeclaration.Variant -> {
                q.addLine(line = "sealed class ${clazz.identifier} {")
                q.indentAndApply {
                    val map = dec.map
                    val genericsInfo = clazz.identifier.genericsInfo
                    for ((name, associatedType) in map) {
                        val genericsInfoStr = when {
                            genericsInfo.isEmpty() -> ""
                            associatedType == null -> {
                                val temp = arrayListOf<TypeExpr>()
                                repeat(times = genericsInfo.size) {
                                    temp.add(TypeExpr.Identifier(type = "Nothing"))
                                }
                                temp.joinToGenericsInfoString()
                            }
                            else -> {
                                val temp = arrayListOf<TypeExpr>()
                                for (genericPlaceholder in genericsInfo) {
                                    if (associatedType.containsIdentifier(genericPlaceholder)) {
                                        genericPlaceholder
                                    } else {
                                        "Nothing"
                                    }.run { temp.add(TypeExpr.Identifier(type = this)) }
                                }
                                temp.joinToGenericsInfoString()
                            }
                        }
                        if (associatedType == null) {
                            addLine(line = "object $name$genericsInfoStr: ${clazz.identifier}()")
                            addEmptyLine()
                        } else {
                            // Single Arg
                            addLine(line = "class $name$genericsInfoStr(val data: $associatedType)")
                        }
                    }
                    visit(q = this, members = clazz.members)
                }
                q.addLine(line = "}")
            }
            is TypeDeclaration.Struct -> {
                q.addLine(line = "class ${clazz.identifier} (")
                q.indentAndApply {
                    val map = dec.map
                    val l = map.size
                    var i = 1
                    for ((name, expr) in map) {
                        if (i == l) {
                            q.addLine(line = "val $name: $expr")
                        } else {
                            q.addLine(line = "val $name: $expr,")
                        }
                        i++
                    }
                }
                q.addLine(line = ") {")
                q.indentAndApply {
                    val args = dec.map.asSequence()
                            .joinToString(separator = ", ") { (n, e) -> "$n: $e = this.$n" }
                    val values = dec.map.asSequence()
                            .joinToString(separator = ", ") { (n, _) -> "$n = $n" }
                    addLine(line = "fun copy($args): ${clazz.identifier} =")
                    indentAndApply {
                        addLine(line = "${clazz.identifier.name}($values)")
                    }
                    addEmptyLine()
                    visit(q = this, members = clazz.members)
                }
                q.addLine(line = "}")
            }
        }
        q.addEmptyLine()
    }

    override fun visit(q: IndentationQueue, members: DecoratedClassMembers) {
        q.addLine(line = "companion object {")
        q.indentAndApply {
            members.constantMembers.forEach { visit(q = this, constantMember = it) }
            members.functionMembers.forEach { visit(q = this, functionMember = it) }
        }
        members.nestedClassMembers.forEach { visit(q = q, clazz = it) }
        q.addLine(line = "}")
    }

    override fun visit(q: IndentationQueue, constantMember: DecoratedClassConstantMember) {
        val public = if (constantMember.isPublic) "" else "private "
        q.addLine(line = "${public}val ${constantMember.identifier}: ${constantMember.type} =")
        q.indentAndApply { visit(q = this, expression = constantMember.expr) }
        q.addEmptyLine()
    }

    override fun visit(q: IndentationQueue, functionMember: DecoratedClassFunctionMember) {
        val public = if (functionMember.isPublic) "" else "private "
        val generics = functionMember.genericsDeclaration
                .takeIf { it.isNotEmpty() }
                ?.joinToGenericsInfoString()
                ?.let { " $it" }
                ?: ""
        val id = functionMember.identifier
        val argumentsString = functionMember.arguments
                .joinToString(separator = ", ") { (i, t) -> "$i: $t" }
        val r = functionMember.returnType
        q.addLine(line = "${public}fun$generics $id($argumentsString): $r =")
        q.indentAndApply { visit(q = this, expression = functionMember.body) }
        q.addEmptyLine()
    }

    override fun visit(q: IndentationQueue, expression: DecoratedExpression) {
        when (expression) {
            is DecoratedExpression.Literal -> q.addLine(line = when (expression.literal) {
                Literal.Unit -> "Unit"
                else -> expression.literal.toString()
            })
            is VariableIdentifier -> {
                val genericsInfo = expression.genericInfo
                        .takeIf { it.isNotEmpty() }
                        ?.joinToGenericsInfoString()
                        ?: ""
                q.addLine(line = expression.variable + genericsInfo)
            }
            is Constructor.NoArgVariant -> q.addLine(
                    line = "${expression.typeName}.${expression.variantName}"
            )
            is Constructor.OneArgVariant -> {
                val dataStr = expression.data.toInlineTranspiledCode(visitor = this)
                q.addLine(line = "${expression.typeName}($dataStr)")
            }
            is Constructor.Struct -> {
                val args = expression.declarations.map { (n, e) ->
                    "$n = ${e.toInlineTranspiledCode(visitor = this)}"
                }.joinToString(separator = ", ")
                q.addLine(line = "${expression.typeName}($args)")
            }
            is Constructor.StructWithCopy -> {
                val oldStr = expression.old.toInlineTranspiledCode(visitor = this)
                val args = expression.newDeclarations.map { (n, e) ->
                    "$n = ${e.toInlineTranspiledCode(visitor = this)}"
                }.joinToString(separator = ", ")
                q.addLine(line = "($oldStr).copy($args)")
            }
            is StructMemberAccess -> {
                val s = expression.structExpr.toInlineTranspiledCode(visitor = this)
                q.addLine(line = "($s).${expression.memberName}")
            }
            is Not -> q.addLine(
                    line = "!(${expression.expr.toInlineTranspiledCode(visitor = this)})"
            )
            is Binary -> {
                val left = expression.left.toInlineTranspiledCode(visitor = this)
                val right = expression.right.toInlineTranspiledCode(visitor = this)
                q.addLine(line = "($left) ${expression.op.symbol} ($right)")
            }
            is Throw -> {
                val e = expression.expr.toInlineTranspiledCode(visitor = this)
                q.addLine(line = "throw PLException($e)")
            }
            is IfElse -> {
                val c = expression.condition.toInlineTranspiledCode(visitor = this)
                q.addLine(line = "if ($c) {")
                q.indentAndApply {
                    visit(q = this, expression = expression.e1)
                }
                q.addLine(line = "} else {")
                q.indentAndApply {
                    if (expression.e2.shouldBeInline) {
                        addLine(line = expression.e2.toInlineTranspiledCode(
                                visitor = this@KotlinTranspilerVisitor
                        ))
                    } else {
                        visit(q = this, expression = expression.e2)
                    }
                }
                q.addLine(line = "}")
            }
            is Match -> {
                val matchedStr = expression.exprToMatch.toInlineTranspiledCode(visitor = this)
                q.addLine(line = "with($matchedStr){ when(it) {")
                q.indentAndApply {
                    val list = expression.matchingList
                    for ((pattern, expr) in expression.matchingList) {
                        when (pattern) {
                            is DecoratedPattern.Variant -> {
                                addLine(line = "is ${pattern.variantIdentifier} -> {")
                                indentAndApply {
                                    pattern.associatedVariable?.let { v ->
                                        addLine(line = "val $v = it.data;")
                                    }
                                    visit(q = this, expression = expr)
                                }
                            }
                            is DecoratedPattern.Variable -> {
                                addLine(line = "else -> {")
                                indentAndApply {
                                    addLine(line = "val ${pattern.identifier} = it.data;")
                                    visit(q = this, expression = expr)
                                }
                            }
                            is DecoratedPattern.WildCard -> {
                                addLine(line = "else -> {")
                                indentAndApply { visit(q = this, expression = expr) }
                            }
                        }
                        addLine(line = "}")
                    }
                }
                q.addLine(line = "}}")
            }
            is FunctionApplication -> {
                val funType = expression.functionExpr.type as TypeExpr.Function
                val needParenthesis = expression.functionExpr.hasLowerPrecedence(expression)
                var funStr = expression.functionExpr.toInlineTranspiledCode(visitor = this)
                if (needParenthesis) {
                    funStr = "($funStr)"
                }
                val shorterLen = expression.arguments.size
                val longerLen = funType.argumentTypes.size
                if (longerLen == shorterLen) {
                    // perfect application
                    val args = expression.arguments
                            .joinToString(separator = ", ") { e ->
                                e.toInlineTranspiledCode(visitor = this)
                            }
                    q.addLine(line = "$funStr($args)")
                } else {
                    // currying
                    val argsInsideLambdaBuilder = StringBuilder()
                    for (i in 0 until shorterLen) {
                        argsInsideLambdaBuilder.append(
                                expression.arguments[i].toInlineTranspiledCode(visitor = this)
                        ).append(", ")
                    }
                    argsInsideLambdaBuilder.setLength(argsInsideLambdaBuilder.length - 2)
                    for (i in shorterLen until longerLen) {
                        argsInsideLambdaBuilder.append(", _tempV").append(i)
                    }
                    val lambdaArgsBuilder = StringBuilder()
                    for (i in shorterLen until longerLen) {
                        lambdaArgsBuilder.append("_tempV").append(i).append(": ")
                                .append(funType.argumentTypes[i])
                                .append(", ")
                    }
                    lambdaArgsBuilder.setLength(lambdaArgsBuilder.length - 2)
                    q.addLine(line = "{ $lambdaArgsBuilder ->")
                    q.indentAndApply {
                        addLine(line = "$funStr($argsInsideLambdaBuilder)")
                    }
                    q.addLine(line = "}")
                }
            }
            is DecoratedExpression.Function -> {
                val args = expression.arguments.asSequence()
                        .joinToString(separator = ", ") { (n, e) -> "$n: $e" }
                q.addLine(line = "{ $args ->")
                q.indentAndApply { visit(q = this, expression = expression.body) }
                q.addLine(line = "}")
            }
            is TryCatch -> {
                q.addLine(line = "try {")
                q.indentAndApply { visit(q = this, expression = expression.tryExpr) }
                q.addLine(line = "} catch (_e: PLException) {")
                q.indentAndApply {
                    addLine(line = "val ${expression.exception} = _e.m;")
                    visit(q = this, expression = expression.catchHandler)
                }
                q.addLine(line = "}")
            }
            is Let -> {
                q.addLine(line = "let ${expression.identifier} = run {")
                q.indentAndApply { visit(q = this, expression = expression.e1) }
                q.addLine(line = "};")
                visit(q = q, expression = expression.e2)
            }
        }
    }

    override fun visit(q: IndentationQueue, pattern: DecoratedPattern): Unit = Unit

    override fun visit(q: IndentationQueue, typeExpr: TypeExpr): Unit = Unit

    override fun visit(q: IndentationQueue, typeIdentifier: TypeIdentifier): Unit = Unit

}
