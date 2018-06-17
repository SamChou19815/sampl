package org.sampl.environment

import com.developersam.fp.FpMap
import org.sampl.ast.common.FunctionCategory
import org.sampl.ast.raw.ClassMember
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeInfo
import org.sampl.ast.type.boolTypeId
import org.sampl.ast.type.charTypeId
import org.sampl.ast.type.floatTypeId
import org.sampl.ast.type.intTypeId
import org.sampl.ast.type.stringArrayTypeId
import org.sampl.ast.type.stringTypeId
import org.sampl.ast.type.unitTypeId

/**
 * [TypeCheckingEnv] is the environment for type checking. It contains a set of currently
 * determined definitions to help type check the program.
 *
 * @param typeDefinitions the set that maps type identifiers to actual types.
 * @param declaredTypes the set of declared types with correctly qualified identifiers.
 * @param typeEnv the type environment with correctly qualified identifiers.
 */
data class TypeCheckingEnv(
        val typeDefinitions: FpMap<String, Pair<List<String>, TypeDeclaration>> = FpMap.empty(),
        val declaredTypes: FpMap<String, List<String>> = FpMap.empty(),
        val typeEnv: FpMap<String, TypeInfo> = FpMap.empty()
) {

    /**
     * [update] creates a new [TypeCheckingEnv] with current level type environment updated
     * to [newTypeEnv].
     */
    fun update(newTypeEnv: FpMap<String, TypeInfo>): TypeCheckingEnv = copy(typeEnv = newTypeEnv)

    /**
     * [get] returns the optionally existing type information for the given
     * [variable], with potentially fully-qualified name.
     */
    operator fun get(variable: String): TypeInfo? = typeEnv[variable]

    /**
     * [put] creates a new [TypeCheckingEnv] that has the current level
     * type environment updated with a new pair [variable] to [typeInfo].
     */
    fun put(variable: String, typeInfo: TypeInfo): TypeCheckingEnv =
            update(newTypeEnv = typeEnv.put(variable, typeInfo))

    /**
     * [enterClass] produces a new [TypeCheckingEnv] with all the public information preserved and
     * make all the types declared in the class available. Type checking is not done here.
     */
    fun enterClass(clazz: ClassMember.Clazz): TypeCheckingEnv = TypeCheckingEnv(
            typeDefinitions = typeDefinitions.put(
                    key = clazz.identifier.name,
                    value = clazz.identifier.genericsInfo to clazz.declaration
            ),
            declaredTypes = declaredTypes.put(
                    key = clazz.identifier.name, value = clazz.identifier.genericsInfo
            ),
            typeEnv = typeEnv
    )

    /**
     * [exitClass] produces a new [TypeCheckingEnv] with all the public information preserved and
     * make the access of current class elements prefixed with class name.
     */
    fun exitClass(clazz: ClassMember.Clazz): TypeCheckingEnv {
        val className = clazz.identifier.name
        // remove added type definitions
        val removedTypeDefinitions = typeDefinitions.remove(key = clazz.identifier.name)
        var currentEnv = this.copy(typeDefinitions = removedTypeDefinitions)
        for (member in clazz.members) {
            currentEnv = when (member) {
                is ClassMember.Constant -> {
                    val name = member.identifier
                    var typeEnv = currentEnv.typeEnv
                    typeEnv = if (member.isPublic) {
                        val v = currentEnv[name] ?: error(message = "Impossible. Name: $name")
                        typeEnv.remove(key = name)
                                .put(key = "$className.$name", value = v)
                    } else {
                        typeEnv
                                .remove(key = name)
                    }
                    currentEnv.update(newTypeEnv = typeEnv)
                }
                is ClassMember.FunctionGroup -> {
                    val typeEnv = member.functions.fold(initial = currentEnv.typeEnv) { env, f ->
                        if (f.category != FunctionCategory.USER_DEFINED) {
                            env
                        } else {
                            val name = f.identifier
                            if (f.isPublic) {
                                val v = env[name] ?: error(message = "Impossible. Name: $name")
                                env.remove(key = name).put(key = "$className.$name", value = v)
                            } else {
                                env.remove(key = name)
                            }
                        }
                    }
                    currentEnv.update(newTypeEnv = typeEnv)
                }
                is ClassMember.Clazz -> {
                    val subclassNames = clazz.members.mapNotNull { m ->
                        if (m is ClassMember.Clazz) {
                            m.identifier.name
                        } else {
                            null
                        }
                    }
                    val newDeclaredTypes = currentEnv.declaredTypes.asSequence()
                            // when exiting, we need to use fully qualified name.
                            .filter { (name, _) -> name in subclassNames }
                            .fold(initial = currentEnv.declaredTypes) { dec, (name, genericsInfo) ->
                                dec.remove(key = name).put("$className.$name", genericsInfo)
                            }
                    currentEnv.copy(declaredTypes = newDeclaredTypes)
                }
            }
        }
        return currentEnv
    }

    companion object {
        /**
         * [initial] is the initial [TypeCheckingEnv] with predefined types includes.
         */
        val initial: TypeCheckingEnv = TypeCheckingEnv(
                declaredTypes = FpMap.create(
                        unitTypeId.name to unitTypeId.genericsInfo,
                        intTypeId.name to intTypeId.genericsInfo,
                        floatTypeId.name to floatTypeId.genericsInfo,
                        boolTypeId.name to boolTypeId.genericsInfo,
                        charTypeId.name to charTypeId.genericsInfo,
                        stringTypeId.name to stringTypeId.genericsInfo,
                        stringArrayTypeId.name to stringArrayTypeId.genericsInfo
                )
        )
    }

}
