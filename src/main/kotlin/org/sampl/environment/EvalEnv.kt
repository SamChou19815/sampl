package org.sampl.environment

import com.developersam.fp.FpMap
import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.decorated.DecoratedClass
import org.sampl.ast.raw.ClassFunctionMember
import org.sampl.ast.raw.ClassMember
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
fun EvalEnv.exitClass(clazz: DecoratedClass): EvalEnv {
    val m = clazz.members
    // remove private members
    val removeAndChangeMember = { env: EvalEnv, member: ClassMember ->
        if (member is ClassFunctionMember && member.category != FunctionCategory.USER_DEFINED) {
            env
        } else {
            val name = member.name
            if (member.isPublic) {
                val v = env[name] ?: error(message = "Impossible. Name: $name")
                env.remove(key = name).put(key = "${clazz.identifier.name}.$name", value = v)
            } else {
                env.remove(key = name)
            }
        }
    }
    return m.fold(initial = this) { e, oneMemberGroup ->
        e.let { oneMemberGroup.constantMembers.fold(it, removeAndChangeMember) }
                .let { oneMemberGroup.functionMembers.fold(it, removeAndChangeMember) }
    }

}

