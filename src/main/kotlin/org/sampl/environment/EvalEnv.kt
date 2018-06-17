package org.sampl.environment

import com.developersam.fp.FpMap
import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.decorated.DecoratedClassMember
import org.sampl.eval.Value

/**
 * [EvalEnv] is the environment for interpretation. It contains a set of currently determined
 * values to help evaluate the program.
 */
typealias EvalEnv = FpMap<String, Value>

/**
 * [exitClass] returns a new [EvalEnv] after existing the class.
 * In particular, it should rename nested values and remove private stuff.
 */
fun EvalEnv.exitClass(clazz: DecoratedClassMember.Clazz): EvalEnv {
    var currentEnv = this
    val className = clazz.identifier.name
    for (member in clazz.members) {
        if (member is DecoratedClassMember.Constant) {
            val name = member.identifier
            currentEnv = if (member.isPublic) {
                val v = currentEnv[name] ?: error(message = "Impossible. Name: $name")
                currentEnv.remove(key = name).put(key = "$className.$name", value = v)
            } else {
                currentEnv.remove(key = name)
            }
        } else if (member is DecoratedClassMember.FunctionGroup) {
            for (f in member.functions) {
                currentEnv = if (f.category != FunctionCategory.USER_DEFINED) {
                    currentEnv
                } else {
                    val name = f.identifier
                    if (f.isPublic) {
                        val v = currentEnv[name] ?: error(message = "Impossible. Name: $name")
                        currentEnv.remove(key = name).put(key = "$className.$name", value = v)
                    } else {
                        currentEnv.remove(key = name)
                    }
                }
            }
        }
    }
    return currentEnv
}
