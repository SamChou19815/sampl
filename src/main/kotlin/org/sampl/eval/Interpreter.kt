package org.sampl.eval

import com.developersam.fp.FpMap
import org.sampl.ast.common.BinaryOperator.AND
import org.sampl.ast.common.BinaryOperator.DIV
import org.sampl.ast.common.BinaryOperator.F_DIV
import org.sampl.ast.common.BinaryOperator.F_MINUS
import org.sampl.ast.common.BinaryOperator.F_MUL
import org.sampl.ast.common.BinaryOperator.F_PLUS
import org.sampl.ast.common.BinaryOperator.GE
import org.sampl.ast.common.BinaryOperator.GT
import org.sampl.ast.common.BinaryOperator.LAND
import org.sampl.ast.common.BinaryOperator.LE
import org.sampl.ast.common.BinaryOperator.LOR
import org.sampl.ast.common.BinaryOperator.LT
import org.sampl.ast.common.BinaryOperator.MINUS
import org.sampl.ast.common.BinaryOperator.MOD
import org.sampl.ast.common.BinaryOperator.MUL
import org.sampl.ast.common.BinaryOperator.OR
import org.sampl.ast.common.BinaryOperator.PLUS
import org.sampl.ast.common.BinaryOperator.SHL
import org.sampl.ast.common.BinaryOperator.SHR
import org.sampl.ast.common.BinaryOperator.STRUCT_EQ
import org.sampl.ast.common.BinaryOperator.STRUCT_NE
import org.sampl.ast.common.BinaryOperator.STR_CONCAT
import org.sampl.ast.common.BinaryOperator.USHR
import org.sampl.ast.common.BinaryOperator.XOR
import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.common.Literal
import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.decorated.DecoratedClassConstantMember
import org.sampl.ast.decorated.DecoratedClassFunctionMember
import org.sampl.ast.decorated.DecoratedClassMembers
import org.sampl.ast.decorated.DecoratedExpression
import org.sampl.ast.decorated.DecoratedPattern
import org.sampl.ast.decorated.DecoratedProgram
import org.sampl.ast.type.TypeExpr
import org.sampl.environment.EvalEnv
import org.sampl.environment.exitClass
import org.sampl.exceptions.PLException
import org.sampl.runtime.PrimitiveRuntimeLibrary

/**
 * [Interpreter] is responsible for evaluating the program ASTs and give a value.
 * It does not need to worry about type checking issues.
 *
 * @param program the program to evaluate.
 */
class Interpreter(private val program: DecoratedProgram) {

    /**
     * [eval] evaluates the given program given in the constructor.
     */
    fun eval(): Value {
        val completeEnv = eval(env = FpMap.empty(), node = program.clazz)
        val mainFunction = program.clazz.members.map { it.functionMembers }.flatten()
                .firstOrNull { member ->
                    member.isPublic && member.identifier == "main" && member.arguments.isEmpty()
                } ?: return UnitValue
        return mainFunction.body.eval(env = completeEnv)
    }

    /**
     * [eval] evaluates the given [node] to a new environment under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedClass): EvalEnv =
            node.members.fold(initial = env) { e, m -> eval(env = e, node = m) }

    /**
     * [eval] evaluates the given [node] to a new environment under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedClassMembers): EvalEnv = env
            .let { node.constantMembers.fold(initial = it, operation = ::eval) }
            .let { evalFunctions(env = it, nodes = node.functionMembers) }
            .let { evalClasses(env = it, nodes = node.nestedClassMembers) }

    /**
     * [eval] evaluates the given [node] to a new environment under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedClassConstantMember): EvalEnv =
            env.put(key = node.identifier, value = node.expr.eval(env = env))

    /**
     * [eval] evaluates the given [nodes] to a new environment under the given [env].
     */
    private fun evalFunctions(env: EvalEnv, nodes: List<DecoratedClassFunctionMember>): EvalEnv {
        var e = env
        val closures = ArrayList<ClosureValue>(nodes.size)
        for (f in nodes) {
            val closure = ClosureValue(
                    category = f.category, name = f.identifier, environment = e,
                    arguments = f.arguments.map { it.first }, code = f.body
            )
            closures.add(element = closure)
            e = e.put(key = f.identifier, value = closure)
        }
        closures.forEach { it.environment = e }
        return e
    }

    /**
     * [eval] evaluates the given [nodes] to a new environment under the given [env].
     */
    private fun evalClasses(env: EvalEnv, nodes: List<DecoratedClass>): EvalEnv =
            nodes.fold(initial = env) { e, clazz -> eval(e, clazz).exitClass(clazz = clazz) }

    /**
     * [eval] evaluates this node to a value under the given [env].
     */
    private fun DecoratedExpression.eval(env: EvalEnv): Value = when (this) {
        is DecoratedExpression.Dummy -> error(message = "Must be a programmer error!")
        is DecoratedExpression.Literal -> eval(node = this)
        is DecoratedExpression.VariableIdentifier -> eval(env = env, node = this)
        is DecoratedExpression.Constructor -> eval(env = env, node = this)
        is DecoratedExpression.StructMemberAccess -> eval(env = env, node = this)
        is DecoratedExpression.Not -> eval(env = env, node = this)
        is DecoratedExpression.Binary -> eval(env = env, node = this)
        is DecoratedExpression.Throw -> eval(env = env, node = this)
        is DecoratedExpression.IfElse -> eval(env = env, node = this)
        is DecoratedExpression.Match -> eval(env = env, node = this)
        is DecoratedExpression.FunctionApplication -> eval(env = env, node = this)
        is DecoratedExpression.Function -> eval(env = env, node = this)
        is DecoratedExpression.TryCatch -> eval(env = env, node = this)
        is DecoratedExpression.Let -> eval(env = env, node = this)
    }

    /**
     * [eval] evaluates the given [node] to a value.
     */
    private fun eval(node: DecoratedExpression.Literal): Value =
            when (node.literal) {
                is Literal.Unit -> UnitValue
                is Literal.Int -> IntValue(value = node.literal.value)
                is Literal.Float -> FloatValue(value = node.literal.value)
                is Literal.Bool -> BoolValue(value = node.literal.value)
                is Literal.Char -> CharValue(value = node.literal.value)
                is Literal.String -> StringValue(value = node.literal.value)
            }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.VariableIdentifier): Value =
            env[node.variable] ?: error(message = "Variable ${node.variable} not found!")

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Constructor): Value = when (node) {
        is DecoratedExpression.Constructor.NoArgVariant -> VariantValue(
                variantIdentifier = node.variantName, associatedValue = null
        )
        is DecoratedExpression.Constructor.OneArgVariant -> VariantValue(
                variantIdentifier = node.variantName,
                associatedValue = node.data.eval(env = env)
        )
        is DecoratedExpression.Constructor.Struct ->
            node.declarations.mapValues { (_, expr) -> expr.eval(env = env) }
                    .let { StructValue(nameValueMap = it) }
        is DecoratedExpression.Constructor.StructWithCopy -> {
            val currentValue = node.old.eval(env = env) as StructValue
            currentValue.nameValueMap.mapValues { (name, currentValue) ->
                node.newDeclarations[name]?.eval(env = env) ?: currentValue
            }.let { StructValue(nameValueMap = it) }
        }
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.StructMemberAccess): Value {
        val struct = node.structExpr.eval(env = env) as StructValue
        return struct.nameValueMap[node.memberName]!!
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Not): Value {
        val bool = node.expr.eval(env = env) as BoolValue
        return BoolValue(value = !bool.value)
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Binary): Value {
        val leftValue = node.left.eval(env = env)
        val rightValue = node.right.eval(env = env)
        return when (node.op) {
            SHL -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value shl rightInt.value.toInt())
            }
            SHR -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value shr rightInt.value.toInt())
            }
            USHR -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value ushr rightInt.value.toInt())
            }
            XOR -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value xor rightInt.value)
            }
            LAND -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value and rightInt.value)
            }
            LOR -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value or rightInt.value)
            }
            MUL -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value * rightInt.value)
            }
            DIV -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value / rightInt.value)
            }
            MOD -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value % rightInt.value)
            }
            PLUS -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value + rightInt.value)
            }
            MINUS -> {
                val leftInt = leftValue as IntValue
                val rightInt = rightValue as IntValue
                IntValue(value = leftInt.value - rightInt.value)
            }
            F_MUL -> {
                val leftFloat = leftValue as FloatValue
                val rightFloat = rightValue as FloatValue
                FloatValue(value = leftFloat.value * rightFloat.value)
            }
            F_DIV -> {
                val leftFloat = leftValue as FloatValue
                val rightFloat = rightValue as FloatValue
                FloatValue(value = leftFloat.value / rightFloat.value)
            }
            F_PLUS -> {
                val leftFloat = leftValue as FloatValue
                val rightFloat = rightValue as FloatValue
                FloatValue(value = leftFloat.value + rightFloat.value)
            }
            F_MINUS -> {
                val leftFloat = leftValue as FloatValue
                val rightFloat = rightValue as FloatValue
                FloatValue(value = leftFloat.value - rightFloat.value)
            }
            STR_CONCAT -> {
                val leftString = leftValue as StringValue
                val rightString = rightValue as StringValue
                StringValue(value = leftString.value + rightString.value)
            }
            LT -> when {
                leftValue == UnitValue && rightValue == UnitValue -> BoolValue(value = false)
                leftValue is IntValue && rightValue is IntValue ->
                    BoolValue(value = leftValue.value < rightValue.value)
                leftValue is FloatValue && rightValue is FloatValue ->
                    BoolValue(value = leftValue.value < rightValue.value)
                leftValue is BoolValue && rightValue is BoolValue ->
                    BoolValue(value = leftValue.value < rightValue.value)
                leftValue is CharValue && rightValue is CharValue ->
                    BoolValue(value = leftValue.value < rightValue.value)
                leftValue is StringValue && rightValue is StringValue ->
                    BoolValue(value = leftValue.value < rightValue.value)
                else -> error(message = "There is a type checking error!")
            }
            LE -> when {
                leftValue == UnitValue && rightValue == UnitValue -> BoolValue(value = true)
                leftValue is IntValue && rightValue is IntValue ->
                    BoolValue(value = leftValue.value <= rightValue.value)
                leftValue is FloatValue && rightValue is FloatValue ->
                    BoolValue(value = leftValue.value <= rightValue.value)
                leftValue is BoolValue && rightValue is BoolValue ->
                    BoolValue(value = leftValue.value <= rightValue.value)
                leftValue is CharValue && rightValue is CharValue ->
                    BoolValue(value = leftValue.value <= rightValue.value)
                leftValue is StringValue && rightValue is StringValue ->
                    BoolValue(value = leftValue.value <= rightValue.value)
                else -> error(message = "There is a type checking error!")
            }
            GT -> when {
                leftValue == UnitValue && rightValue == UnitValue -> BoolValue(value = false)
                leftValue is IntValue && rightValue is IntValue ->
                    BoolValue(value = leftValue.value > rightValue.value)
                leftValue is FloatValue && rightValue is FloatValue ->
                    BoolValue(value = leftValue.value > rightValue.value)
                leftValue is BoolValue && rightValue is BoolValue ->
                    BoolValue(value = leftValue.value > rightValue.value)
                leftValue is CharValue && rightValue is CharValue ->
                    BoolValue(value = leftValue.value > rightValue.value)
                leftValue is StringValue && rightValue is StringValue ->
                    BoolValue(value = leftValue.value > rightValue.value)
                else -> error(message = "There is a type checking error!")
            }
            GE -> when {
                leftValue == UnitValue && rightValue == UnitValue -> BoolValue(value = true)
                leftValue is IntValue && rightValue is IntValue ->
                    BoolValue(value = leftValue.value >= rightValue.value)
                leftValue is FloatValue && rightValue is FloatValue ->
                    BoolValue(value = leftValue.value >= rightValue.value)
                leftValue is BoolValue && rightValue is BoolValue ->
                    BoolValue(value = leftValue.value >= rightValue.value)
                leftValue is CharValue && rightValue is CharValue ->
                    BoolValue(value = leftValue.value >= rightValue.value)
                leftValue is StringValue && rightValue is StringValue ->
                    BoolValue(value = leftValue.value >= rightValue.value)
                else -> error(message = "There is a type checking error!")
            }
            STRUCT_EQ -> BoolValue(value = leftValue == rightValue)
            STRUCT_NE -> BoolValue(value = leftValue != rightValue)
            AND -> {
                val leftBool = leftValue as BoolValue
                val rightBool = rightValue as BoolValue
                BoolValue(value = leftBool.value && rightBool.value)
            }
            OR -> {
                val leftBool = leftValue as BoolValue
                val rightBool = rightValue as BoolValue
                BoolValue(value = leftBool.value || rightBool.value)
            }
        }
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Throw): Value {
        val stringValue = node.eval(env = env) as StringValue
        throw PLException(m = stringValue.value)
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.IfElse): Value {
        val conditionValue = node.condition.eval(env = env) as BoolValue
        val toEval = if (conditionValue.value) node.e1 else node.e2
        return toEval.eval(env = env)
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Match): Value {
        val valueToMatch = node.exprToMatch.eval(env = env)
        loop@ for ((pattern, expr) in node.matchingList) {
            when (pattern) {
                is DecoratedPattern.Variant -> {
                    if (valueToMatch is VariantValue &&
                            valueToMatch.variantIdentifier == pattern.variantIdentifier) {
                        val newEnv = if (pattern.associatedVariable != null &&
                                valueToMatch.associatedValue != null) {
                            env.put(pattern.associatedVariable, valueToMatch.associatedValue)
                        } else env
                        expr.eval(env = newEnv)
                    } else {
                        continue@loop
                    }
                }
                is DecoratedPattern.Variable -> expr.eval(env = env.put(
                        key = pattern.identifier, value = valueToMatch
                ))
                DecoratedPattern.WildCard -> expr.eval(env = env)
            }
        }
        error(message = "Impossible")
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.FunctionApplication): Value {
        val closure = node.functionExpr.eval(env = env) as ClosureValue
        val argumentValues = node.arguments.map { it.eval(env = env) }
        val namesToValues = closure.arguments.zip(argumentValues)
        val argumentsSize = closure.arguments.size
        val valuesSize = argumentValues.size
        return when {
            argumentsSize == valuesSize -> {
                // Exact application
                val newEnv = namesToValues.fold(closure.environment) { e, (n, v) -> e.put(n, v) }
                when (closure.category) {
                    FunctionCategory.PRIMITIVE -> {
                        // Improve performance later
                        PrimitiveRuntimeLibrary.invokeFunction(
                                name = closure.name!!, arguments = argumentValues
                        )
                    }
                    FunctionCategory.PROVIDED -> {
                        if (program.providedRuntimeLibrary == null) {
                            error(message = "Impossible")
                        }
                        program.providedRuntimeLibrary.invokeFunction(
                                name = closure.name!!, arguments = argumentValues
                        )
                    }
                    FunctionCategory.USER_DEFINED -> closure.code.eval(env = newEnv)
                }
            }
            argumentsSize > valuesSize -> {
                // Need curring, don't care its category.
                val newFunctionArguments = closure.arguments
                        .subList(fromIndex = valuesSize, toIndex = argumentsSize)
                val newFunctionArgTypes = (node.functionExpr.type as TypeExpr.Function)
                        .argumentTypes.subList(fromIndex = valuesSize, toIndex = argumentsSize)
                val newFunctionsAnnotatedArgs = newFunctionArguments.zip(newFunctionArgTypes)
                        .map { (name, type) ->
                            DecoratedExpression.VariableIdentifier(
                                    variable = name, genericInfo = emptyList(), type = type
                            )
                        }
                val functionApplicationExpr = DecoratedExpression.FunctionApplication(
                        functionExpr = node.functionExpr,
                        arguments = node.arguments + newFunctionsAnnotatedArgs,
                        type = node.type
                )
                ClosureValue(
                        category = FunctionCategory.USER_DEFINED, environment = env,
                        arguments = newFunctionArguments,
                        code = functionApplicationExpr
                )
            }
            else -> error(message = "Bad case. There must be an error in the type checker.")
        }
    }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Function): Value = ClosureValue(
            category = FunctionCategory.USER_DEFINED, environment = env,
            arguments = node.arguments.map { it.first }, code = node.body
    )

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.TryCatch): Value =
            try {
                node.tryExpr.eval(env = env)
            } catch (e: PLException) {
                val errorValue = StringValue(value = e.m)
                val newEnv = env.put(key = node.exception, value = errorValue)
                node.catchHandler.eval(env = newEnv)
            }

    /**
     * [eval] evaluates the given [node] to a value under the given [env].
     */
    private fun eval(env: EvalEnv, node: DecoratedExpression.Let): Value {
        val v1 = node.e1.eval(env = env)
        val newEnv = env.put(key = node.identifier, value = v1)
        return node.e2.eval(env = newEnv)
    }

}
