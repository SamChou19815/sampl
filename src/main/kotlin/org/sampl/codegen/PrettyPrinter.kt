package org.sampl.codegen

import org.sampl.ast.common.FunctionCategory.USER_DEFINED
import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassFunctionMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedPattern
import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.type.TypeDeclaration
import org.sampl.util.joinToGenericsInfoString

/**
 * [PrettyPrinter] is responsible for pretty printing a program node.
 */
class PrettyPrinter private constructor() : AstToCodeConverter {

    /**
     * [q] is the only indentation queue used in this class.
     */
    private val q: IdtQueue = IdtQueue(strategy = IdtStrategy.TWO_SPACES)

    /**
     * [CodeConvertible.toOneLineCode] returns the one-liner form of the [CodeConvertible].
     */
    private fun CodeConvertible.toOneLineCode(): String =
            PrettyPrinter().apply { acceptConversion(converter = this) }.q.toOneLineCode()

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

    override fun convert(node: DecoratedProgram): Unit = convert(node = node.clazz)

    override fun convert(node: DecoratedClass) {
        if (node.declaration.isEmpty && node.members.isEmpty) {
            q.addLine(line = "class ${node.identifier}")
            return
        } else if (node.declaration.isEmpty) {
            q.addLine(line = "class ${node.identifier} {")
            q.addEmptyLine()
            q.indentAndApply { convert(node = node.members) }
            q.addLine(line = "}")
            return
        } else if (node.members.isEmpty) {
            q.addLine(line = "class ${node.identifier} (")
            q.indentAndApply { convert(node = node.declaration) }
            q.addLine(line = ")")
        } else {
            q.addLine(line = "class ${node.identifier} (")
            q.indentAndApply { convert(node = node.declaration) }
            q.addLine(line = ") {")
            q.addEmptyLine()
            q.indentAndApply { convert(node = node.members) }
            q.addLine(line = "}")
        }
    }

    private fun convert(node: TypeDeclaration): Unit = when (node) {
        is TypeDeclaration.Variant -> {
            node.map.forEach { (name, expr) ->
                val line = StringBuilder()
                        .append("| ").append(name)
                        .apply {
                            if (expr != null) {
                                append(" of ").append(expr.toString())
                            }
                        }.toString()
                q.addLine(line = line)
            }
        }
        is TypeDeclaration.Struct -> {
            val l = node.map.size
            var i = 1
            for ((name, expr) in node.map) {
                if (i == l) {
                    q.addLine(line = "$name: $expr")
                } else {
                    q.addLine(line = "$name: $expr,")
                }
                i++
            }
        }
    }

    override fun convert(node: DecoratedClassMembers) {
        val printerAction: (CodeConvertible) -> Unit = { m ->
            m.acceptConversion(converter = this)
            q.addEmptyLine()
        }
        node.constantMembers.forEach(action = printerAction)
        node.functionMembers.filter { it.category == USER_DEFINED }.forEach(action = printerAction)
        node.nestedClassMembers.forEach(action = printerAction)
    }

    override fun convert(node: DecoratedClassConstantMember) {
        val header = StringBuilder().apply {
            if (!node.isPublic) {
                append("private ")
            }
            append("let ").append(node.identifier).append(" =")
        }.toString()
        q.addLine(line = header)
        q.indentAndApply { node.expr.acceptConversion(converter = this@PrettyPrinter) }
    }

    override fun convert(node: DecoratedClassFunctionMember) {
        val header = StringBuilder().apply {
            if (!node.isPublic) {
                append("private ")
            }
            append("fun ")
            if (node.genericsDeclaration.isNotEmpty()) {
                append(node.genericsDeclaration.joinToString(
                        separator = ", ", prefix = "<", postfix = "> "
                ))
            }
            append(node.identifier)
            node.arguments.joinToString(separator = ", ", prefix = "(", postfix = ")") { (n, t) ->
                "$n: $t"
            }.run { append(this) }
            append(": ").append(node.returnType.toString()).append(" =")
        }.toString()
        q.addLine(line = header)
        q.indentAndApply { node.body.acceptConversion(converter = this@PrettyPrinter) }
    }

    override fun convert(node: DecoratedExpression.Literal) {
        q.addLine(line = node.literal.toString())
    }

    override fun convert(node: DecoratedExpression.VariableIdentifier) {
        val line = StringBuilder()
                .apply {
                    append(node.variable)
                    if (node.genericInfo.isNotEmpty()) {
                        append(node.genericInfo.joinToGenericsInfoString())
                    }
                }.toString()
        q.addLine(line = line)
    }

    override fun convert(node: DecoratedExpression.Constructor): Unit = when (node) {
        is DecoratedExpression.Constructor.NoArgVariant -> {
            val line = StringBuilder().apply {
                append(node.typeName).append('.').append(node.variantName)
                if (node.genericInfo.isNotEmpty()) {
                    append(node.genericInfo.joinToGenericsInfoString())
                }
            }.toString()
            q.addLine(line = line)
        }
        is DecoratedExpression.Constructor.OneArgVariant -> {
            val line = StringBuilder().append(node.typeName)
                    .append('.').append(node.variantName)
                    .append(" (").append(node.data.toOneLineCode()).append(")")
                    .toString()
            q.addLine(line = line)
        }
        is DecoratedExpression.Constructor.Struct -> {
            q.addLine(line = "${node.typeName} {")
            q.indentAndApply {
                for ((name, expr) in node.declarations) {
                    addLine(line = "$name = ${expr.toOneLineCode()};")
                }
            }
            q.addLine(line = "}")
        }
        is DecoratedExpression.Constructor.StructWithCopy -> {
            q.addLine(line = "{")
            val oldStructCode = node.old.toOneLineCode(parent = node)
            q.indentAndApply {
                addLine(line = "$oldStructCode with")
                for ((name, expr) in node.newDeclarations) {
                    val exprCode = expr.toOneLineCode(parent = node)
                    addLine(line = "$name = $exprCode")
                }
            }
            q.addLine(line = "}")
        }
    }

    override fun convert(node: DecoratedExpression.StructMemberAccess) {
        val structExprCode = node.structExpr.toOneLineCode(parent = node)
        q.addLine(line = "$structExprCode.${node.memberName}")
    }

    override fun convert(node: DecoratedExpression.Not) {
        val exprCode = node.expr.toOneLineCode(parent = node)
        q.addLine(line = "!$exprCode")
    }

    override fun convert(node: DecoratedExpression.Binary) {
        val leftCode = node.left.toOneLineCode(parent = node)
        val rightCode = node.right.toOneLineCode(parent = node)
        q.addLine(line = "$leftCode ${node.op.symbol} $rightCode")
    }

    override fun convert(node: DecoratedExpression.Throw) {
        val exprCode = node.expr.toOneLineCode(parent = node)
        q.addLine(line = "throw<${node.type}> $exprCode")
    }

    override fun convert(node: DecoratedExpression.IfElse) {
        q.addLine(line = "if (${node.condition.toOneLineCode()}) then (")
        q.indentAndApply { node.e1.acceptConversion(converter = this@PrettyPrinter) }
        q.addLine(line = ") else (")
        q.indentAndApply { node.e2.acceptConversion(converter = this@PrettyPrinter) }
        q.addLine(line = ")")
    }

    override fun convert(node: DecoratedExpression.Match) {
        val matchedCode = node.exprToMatch.toOneLineCode(parent = node)
        q.addLine(line = "match $matchedCode with")
        for ((pattern, expr) in node.matchingList) {
            val lineCommon = "| ${pattern.toPatternCode()} ->"
            val action: IdtQueue.() -> Unit =
                    { expr.acceptConversion(converter = this@PrettyPrinter) }
            if (expr.hasLowerPrecedence(parent = node)) {
                q.addLine(line = "$lineCommon (")
                q.indentAndApply(action = action)
                q.addLine(line = ")")
            } else {
                q.addLine(line = lineCommon)
                q.indentAndApply(action = action)
            }
        }
    }

    /**
     * [DecoratedPattern.toPatternCode] returns the code form of this pattern.
     */
    private fun DecoratedPattern.toPatternCode(): String = when (this) {
        is DecoratedPattern.Variant -> {
            StringBuilder().apply {
                append(variantIdentifier)
                if (associatedVariable != null) {
                    append(' ').append(associatedVariable)
                }
            }.toString()
        }
        is DecoratedPattern.Variable -> identifier
        is DecoratedPattern.WildCard -> "_"
    }

    override fun convert(node: DecoratedExpression.FunctionApplication) {
        val functionCode = node.functionExpr.toOneLineCode(parent = node)
        val argumentCode = node.arguments.joinToString(
                separator = ", ", prefix = "(", postfix = ")"
        ) { it.toOneLineCode() }
        q.addLine(line = "$functionCode$argumentCode")
    }

    override fun convert(node: DecoratedExpression.Function) {
        val header = StringBuilder().append("{").apply {
            node.arguments.joinToString(separator = ", ", prefix = "(", postfix = ")") { (n, t) ->
                "$n: $t"
            }.run { append(this) }
        }.append(" ->").toString()
        q.addLine(line = header)
        q.indentAndApply { node.body.acceptConversion(converter = this@PrettyPrinter) }
        q.addLine(line = "}")
    }

    override fun convert(node: DecoratedExpression.TryCatch) {
        q.addLine(line = "try (")
        q.indentAndApply { node.tryExpr.acceptConversion(converter = this@PrettyPrinter) }
        q.addLine(line = ") catch ${node.exception} (")
        q.indentAndApply { node.catchHandler.acceptConversion(converter = this@PrettyPrinter) }
        q.addLine(line = ")")
    }

    override fun convert(node: DecoratedExpression.Let) {
        val e1Code = node.e1.toOneLineCode(parent = node)
        q.addLine(line = "let ${node.identifier} = $e1Code;")
        node.e2.acceptConversion(converter = this)
    }

    companion object {

        /**
         * [prettyPrint] returns the given [node] as well-formatted code in string.
         */
        fun prettyPrint(node: CodeConvertible): String =
                PrettyPrinter().apply { node.acceptConversion(converter = this) }.q.toIndentedCode()

    }

}
