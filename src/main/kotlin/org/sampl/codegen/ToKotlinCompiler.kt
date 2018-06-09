package org.sampl.codegen

import org.sampl.EASTER_EGG
import org.sampl.TOP_LEVEL_PROGRAM_NAME
import org.sampl.ast.common.BinaryOperator
import org.sampl.ast.common.Literal
import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassFunctionMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedPattern
import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeExpr
import org.sampl.ast.type.TypeIdentifier
import org.sampl.ast.type.boolTypeExpr
import org.sampl.ast.type.charTypeExpr
import org.sampl.ast.type.floatTypeExpr
import org.sampl.ast.type.intTypeExpr
import org.sampl.ast.type.stringTypeExpr
import org.sampl.ast.type.unitTypeExpr
import org.sampl.util.joinToGenericsInfoString

/**
 * [ToKotlinCompiler] is responsible for compiling the given AST node to valid Kotlin Code.
 */
class ToKotlinCompiler private constructor() : AstToCodeConverter {

    /**
     * [q] is the only indentation queue used in this class.
     */
    private val q: IdtQueue = IdtQueue(strategy = IdtStrategy.FOUR_SPACES)

    // Helper Methods

    /**
     * [CodeConvertible.toOneLineCode] returns the one-liner form of the [CodeConvertible].
     */
    private fun CodeConvertible.toOneLineCode(): String =
            ToKotlinCompiler().apply { acceptConversion(converter = this) }.q.toOneLineCode()

    /**
     * [DecoratedExpression.toOneLineCode] returns the one-liner form of [DecoratedExpression].
     *
     * This method is expression node specific. It will consider the precedence between this node
     * and its [parent] to decide whether to add parenthesis.
     */
    private fun DecoratedExpression.toOneLineCode(parent: DecoratedExpression): String =
            toOneLineCode().let { code ->
                if (hasLowerPrecedence(parent = parent)) "($code)" else code
            }

    /**
     * [TypeExpr.toKotlinType] converts a [TypeExpr] to string form of Kotlin type expression.
     */
    private fun TypeExpr.toKotlinType(): String = when (this) {
        is TypeExpr.Identifier -> when {
            this == unitTypeExpr -> "Unit"
            this == intTypeExpr -> "Long"
            this == floatTypeExpr -> "Double"
            this == boolTypeExpr -> "Boolean"
            this == charTypeExpr -> "Char"
            this == stringTypeExpr -> "String"
            genericsInfo.isEmpty() -> type
            else -> type + genericsInfo.joinToGenericsInfoString()
        }
        is TypeExpr.Function -> {
            val argumentStrings = argumentTypes.joinToString(separator = ", ") { it.toKotlinType() }
            val returnTypeString = returnType.toKotlinType()
            "($argumentStrings) -> $returnTypeString"
        }
    }

    // Visitor Methods

    override fun convert(node: DecoratedProgram) {
        q.addLine(line = """@file:JvmName(name = "$TOP_LEVEL_PROGRAM_NAME")""")
        q.addEmptyLine()
        // Add some nice easter egg :))
        q.addLine(line = "/*")
        q.addLine(line = " * $EASTER_EGG")
        q.addLine(line = " */")
        q.addEmptyLine()
        q.addLine(line = "class PLException(val m: String): RuntimeException(m)")
        q.addEmptyLine()
        val clazz = node.clazz
        convert(node = clazz)
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

    override fun convert(node: DecoratedClass) {
        val i = node.identifier
        val m = node.members
        val d = node.declaration
        when (d) {
            is TypeDeclaration.Variant -> convertClass(identifier = i, members = m, declaration = d)
            is TypeDeclaration.Struct -> convertClass(identifier = i, members = m, declaration = d)
        }
        q.addEmptyLine()
    }

    /**
     * [convertClass] converts a class with [identifier] [members] and [declaration] to well
     * formatted Kotlin code.
     */
    private fun convertClass(identifier: TypeIdentifier, members: DecoratedClassMembers,
                             declaration: TypeDeclaration.Variant) {
        val classRawName = identifier.name
        val classType = if (identifier.genericsInfo.isEmpty()) classRawName else {
            classRawName + identifier.genericsInfo.joinToString(
                    separator = ", ", prefix = "<", postfix = ">") { "out $it" }
        }
        q.addLine(line = "sealed class $classType {")
        q.indentAndApply {
            val map = declaration.map
            val genericsInfo = identifier.genericsInfo
            for ((name, associatedType) in map) {
                val genericsInfoStr = when {
                    genericsInfo.isEmpty() -> ""
                    associatedType == null -> {
                        val temp = arrayListOf<TypeExpr>()
                        repeat(times = genericsInfo.size) {
                            temp.add(TypeExpr.Identifier(type = "Nothing"))
                        }
                        temp.joinToString(separator = ",", prefix = "<", postfix = ">") { t ->
                            t.toKotlinType()
                        }
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
                        temp.joinToString(separator = ",", prefix = "<", postfix = ">") { t ->
                            t.toKotlinType()
                        }
                    }
                }
                if (associatedType == null) {
                    // No Args
                    addLine(line = "object $name$genericsInfoStr: $identifier()")
                    addEmptyLine()
                } else {
                    // Single Arg
                    val dataType = associatedType.toKotlinType()
                    addLine(line = "class $name$genericsInfoStr(val data: $dataType)")
                }
            }
            convert(node = members)
        }
        q.addLine(line = "}")
    }

    /**
     * [convertClass] converts a class with [identifier] [members] and [declaration] to well
     * formatted Kotlin code.
     */
    private fun convertClass(identifier: TypeIdentifier, members: DecoratedClassMembers,
                             declaration: TypeDeclaration.Struct) {
        q.addLine(line = "class $identifier (")
        q.indentAndApply {
            val map = declaration.map
            val l = map.size
            var i = 1
            for ((name, expr) in map) {
                if (i == l) {
                    q.addLine(line = "val $name: ${expr.toKotlinType()}")
                } else {
                    q.addLine(line = "val $name: ${expr.toKotlinType()},")
                }
                i++
            }
        }
        q.addLine(line = ") {")
        q.indentAndApply {
            val args = declaration.map.asSequence().joinToString(separator = ", ") { (n, e) ->
                "$n: ${e.toKotlinType()} = this.$n"
            }
            val values = declaration.map.asSequence().joinToString(separator = ", ") { (n, _) ->
                "$n = $n"
            }
            addLine(line = "fun copy($args): $identifier =")
            indentAndApply {
                addLine(line = "${identifier.name}($values)")
            }
            addEmptyLine()
            convert(node = members)

        }
        q.addLine(line = "}")
    }

    override fun convert(node: DecoratedClassMembers) {
        val compilerAction: (CodeConvertible) -> Unit = { m ->
            m.acceptConversion(converter = this)
            q.addEmptyLine()
        }
        q.addLine(line = "companion object {")
        q.indentAndApply {
            node.constantMembers.forEach(compilerAction)
            node.functionMembers.forEach(compilerAction)
        }
        q.addLine(line = "}")
        node.nestedClassMembers.forEach(compilerAction)
    }

    override fun convert(node: DecoratedClassConstantMember) {
        val public = if (node.isPublic) "" else "private "
        q.addLine(line = "${public}val ${node.identifier}: ${node.type.toKotlinType()} =")
        q.indentAndApply { node.expr.acceptConversion(converter = this@ToKotlinCompiler) }
    }

    override fun convert(node: DecoratedClassFunctionMember) {
        val public = if (node.isPublic) "" else "private "
        val generics = node.genericsDeclaration
                .takeIf { it.isNotEmpty() }
                ?.joinToGenericsInfoString()
                ?.let { " $it" }
                ?: ""
        val id = node.identifier
        val argumentsString = node.arguments
                .joinToString(separator = ", ") { (i, t) -> "$i: ${t.toKotlinType()}" }
        val r = node.returnType.toKotlinType()
        q.addLine(line = "${public}fun$generics $id($argumentsString): $r =")
        q.indentAndApply { node.body.acceptConversion(converter = this@ToKotlinCompiler) }
    }

    override fun convert(node: DecoratedExpression.Literal) {
        q.addLine(line = when (node.literal) {
            Literal.Unit -> "Unit"
            else -> node.literal.toString()
        })
    }

    override fun convert(node: DecoratedExpression.VariableIdentifier) {
        val genericsInfo = node.genericInfo
                .takeIf { it.isNotEmpty() }
                ?.joinToString(separator = ", ", prefix = "<", postfix = ">") { it.toKotlinType() }
                ?: ""
        q.addLine(line = node.variable + genericsInfo)
    }

    override fun convert(node: DecoratedExpression.Constructor) {
        when (node) {
            is DecoratedExpression.Constructor.NoArgVariant -> q.addLine(
                    line = "${node.typeName}.${node.variantName}"
            )
            is DecoratedExpression.Constructor.OneArgVariant -> {
                val dataStr = node.data.toOneLineCode()
                q.addLine(line = "${node.typeName}($dataStr)")
            }
            is DecoratedExpression.Constructor.Struct -> {
                val args = node.declarations.map { (n, e) ->
                    "$n = ${e.toOneLineCode()}"
                }.joinToString(separator = ", ")
                q.addLine(line = "${node.typeName}($args)")
            }
            is DecoratedExpression.Constructor.StructWithCopy -> {
                val oldStr = node.old.toOneLineCode()
                val args = node.newDeclarations.map { (n, e) ->
                    "$n = ${e.toOneLineCode()}"
                }.joinToString(separator = ", ")
                q.addLine(line = "($oldStr).copy($args)")
            }
        }
    }

    override fun convert(node: DecoratedExpression.StructMemberAccess) {
        val s = node.structExpr.toOneLineCode()
        q.addLine(line = "($s).${node.memberName}")
    }

    override fun convert(node: DecoratedExpression.Not) {
        q.addLine(line = "!(${node.expr.toOneLineCode()})")
    }

    override fun convert(node: DecoratedExpression.Binary) {
        val left = node.left.toOneLineCode()
        val right = node.right.toOneLineCode()
        q.addLine(line = "($left) ${node.op.toKotlinForm()} ($right)")
    }

    /**
     * [BinaryOperator.toKotlinForm] maps the binary operator to its form in Kotlin
     */
    private fun BinaryOperator.toKotlinForm(): String = when (this) {
        BinaryOperator.F_MUL -> "*"
        BinaryOperator.F_DIV -> "/"
        BinaryOperator.F_PLUS -> "+"
        BinaryOperator.F_MINUS -> "-"
        BinaryOperator.STR_CONCAT -> "+"
        else -> symbol
    }

    override fun convert(node: DecoratedExpression.Throw) {
        val e = node.expr.toOneLineCode()
        q.addLine(line = "throw PLException($e)")
    }

    override fun convert(node: DecoratedExpression.IfElse) {
        val c = node.condition.toOneLineCode()
        q.addLine(line = "if ($c) {")
        q.indentAndApply { node.e1.acceptConversion(converter = this@ToKotlinCompiler) }
        q.addLine(line = "} else {")
        q.indentAndApply { node.e2.acceptConversion(converter = this@ToKotlinCompiler) }
        q.addLine(line = "}")
    }

    override fun convert(node: DecoratedExpression.Match) {
        val matchedStr = node.exprToMatch.toOneLineCode()
        q.addLine(line = "with($matchedStr){ when(it) {")
        q.indentAndApply {
            for ((pattern, expr) in node.matchingList) {
                when (pattern) {
                    is DecoratedPattern.Variant -> {
                        addLine(line = "is ${pattern.variantIdentifier} -> {")
                        indentAndApply {
                            pattern.associatedVariable?.let { v ->
                                addLine(line = "val $v = it.data;")
                            }
                            expr.acceptConversion(converter = this@ToKotlinCompiler)
                        }
                    }
                    is DecoratedPattern.Variable -> {
                        addLine(line = "else -> {")
                        indentAndApply {
                            addLine(line = "val ${pattern.identifier} = it.data;")
                            expr.acceptConversion(converter = this@ToKotlinCompiler)
                        }
                    }
                    is DecoratedPattern.WildCard -> {
                        addLine(line = "else -> {")
                        indentAndApply { expr.acceptConversion(converter = this@ToKotlinCompiler) }
                    }
                }
                addLine(line = "}")
            }
        }
        q.addLine(line = "}}")
    }

    override fun convert(node: DecoratedExpression.FunctionApplication) {
        val funType = node.functionExpr.type as TypeExpr.Function
        val funStr = node.functionExpr.toOneLineCode(parent = node)
        val shorterLen = node.arguments.size
        val longerLen = funType.argumentTypes.size
        if (longerLen == shorterLen) {
            // perfect application
            val args = node.arguments.joinToString(separator = ", ") { it.toOneLineCode() }
            q.addLine(line = "$funStr($args)")
        } else {
            // currying
            val argsInsideLambdaBuilder = StringBuilder()
            for (i in 0 until shorterLen) {
                argsInsideLambdaBuilder.append(node.arguments[i].toOneLineCode()).append(", ")
            }
            argsInsideLambdaBuilder.setLength(argsInsideLambdaBuilder.length - 2)
            for (i in shorterLen until longerLen) {
                argsInsideLambdaBuilder.append(", _tempV").append(i)
            }
            val lambdaArgsBuilder = StringBuilder()
            for (i in shorterLen until longerLen) {
                lambdaArgsBuilder.append("_tempV").append(i).append(": ")
                        .append(funType.argumentTypes[i].toKotlinType())
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

    override fun convert(node: DecoratedExpression.Function) {
        val args = node.arguments.asSequence().joinToString(separator = ", ") { (name, typeExpr) ->
            "$name: ${typeExpr.toKotlinType()}"
        }
        q.addLine(line = "{ $args ->")
        q.indentAndApply {
            node.body.acceptConversion(converter = this@ToKotlinCompiler)
        }
        q.addLine(line = "}")
    }

    override fun convert(node: DecoratedExpression.TryCatch) {
        q.addLine(line = "try {")
        q.indentAndApply { node.tryExpr.acceptConversion(converter = this@ToKotlinCompiler) }
        q.addLine(line = "} catch (_e: PLException) {")
        q.indentAndApply {
            addLine(line = "val ${node.exception} = _e.m;")
            node.catchHandler.acceptConversion(converter = this@ToKotlinCompiler)
        }
        q.addLine(line = "}")
    }

    override fun convert(node: DecoratedExpression.Let) {
        q.addLine(line = "val ${node.identifier}: ${node.type.toKotlinType()} = run {")
        q.indentAndApply { node.e1.acceptConversion(converter = this@ToKotlinCompiler) }
        q.addLine(line = "};")
        node.e2.acceptConversion(converter = this)
    }

    companion object {

        /**
         * [compile] returns the given [node] as well-formatted Kotlin code in string.
         */
        fun compile(node: CodeConvertible): String =
                ToKotlinCompiler()
                        .apply { node.acceptConversion(converter = this) }
                        .q.toIndentedCode()

    }

}
