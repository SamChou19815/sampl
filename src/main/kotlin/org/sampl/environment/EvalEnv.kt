package org.sampl.environment

import com.developersam.fp.FpMap
import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.decorated.DecoratedClassFunction
import org.sampl.ast.decorated.DecoratedClassMember.Clazz
import org.sampl.ast.decorated.DecoratedClassMember.Constant
import org.sampl.ast.decorated.DecoratedClassMember.FunctionGroup
import org.sampl.eval.Value

/**
 * [EvalEnv] is the environment for interpretation. It contains a set of currently determined
 * values to help evaluate the program.
 */
typealias EvalEnv = FpMap<String, Value>

/**
 * [Constant.processWhenExit] returns a new environment when exiting a class given environment [e]
 * and [className] for reference.
 * It needs to remove all private members and prefix each member and its type by the class name.
 */
private fun Constant.processWhenExit(
        e: EvalEnv, className: String
): EvalEnv =
        if (isPublic) {
            val v = e[identifier] ?: error(message = "Impossible. Name: $identifier")
            e.remove(key = identifier).put(key = "$className.$identifier", value = v)
        } else e.remove(key = identifier)

/**
 * [DecoratedClassFunction.processWhenExit] returns a new environment when exiting a class given
 * environment [e] and [className] for reference.
 * It needs to remove all private members and prefix each member and its type by the class name.
 */
private fun DecoratedClassFunction.processWhenExit(e: EvalEnv, className: String): EvalEnv =
        when {
            category != FunctionCategory.USER_DEFINED -> e
            isPublic -> {
                val v = e[identifier] ?: error(message = "Impossible. Name: $identifier")
                e.remove(key = identifier).put(key = "$className.$identifier", value = v)
            }
            else -> e.remove(key = identifier)
        }

/**
 * [Clazz.processAsWhenExit] returns a new environment when exiting a class given environment [e],
 * and [className] for reference.
 * It needs to prefix all of its child members.
 */
private fun Clazz.processAsWhenExit(e: EvalEnv, className: String): EvalEnv {
    val subclassName = identifier.name
    return e.mapByKey { name ->
        if (name.indexOf(subclassName) == 0) "$className.$name" else name
    }
}

/**
 * [exitClass] returns a new [EvalEnv] after existing the class.
 * In particular, it should rename nested values and remove private stuff.
 */
fun EvalEnv.exitClass(clazz: Clazz): EvalEnv {
    val className = clazz.identifier.name
    return clazz.members.fold(initial = this) { currentEnv, m ->
        when (m) {
            is Constant -> m.processWhenExit(e = currentEnv, className = className)
            is FunctionGroup -> m.functions.fold(initial = currentEnv) { e, f ->
                f.processWhenExit(e = e, className = className)
            }
            is Clazz -> m.processAsWhenExit(e = currentEnv, className = className)
        }
    }
}
