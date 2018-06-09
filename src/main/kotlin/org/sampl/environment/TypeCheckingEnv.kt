package org.sampl.environment

import com.developersam.fp.FpMap
import org.sampl.ast.raw.ClassMember
import org.sampl.ast.raw.Clazz
import org.sampl.ast.type.TypeDeclaration
import org.sampl.ast.type.TypeInfo
import org.sampl.ast.type.boolTypeId
import org.sampl.ast.type.charTypeId
import org.sampl.ast.type.floatTypeId
import org.sampl.ast.type.intTypeId
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
    fun enterClass(clazz: Clazz): TypeCheckingEnv {
        return TypeCheckingEnv(
                typeDefinitions = typeDefinitions.put(
                        key = clazz.name,
                        value = clazz.identifier.genericsInfo to clazz.declaration
                ),
                declaredTypes = declaredTypes.put(
                        key = clazz.name, value = clazz.identifier.genericsInfo
                ),
                typeEnv = typeEnv
        )
    }

    /**
     * [exitClass] produces a new [TypeCheckingEnv] with all the public information preserved and
     * make the access of current class elements prefixed with class name.
     */
    fun exitClass(clazz: Clazz): TypeCheckingEnv {
        val m = clazz.members
        // remove added type definitions
        val removedTypeDefinitions = typeDefinitions.remove(key = clazz.name)
        // remove and change declared types
        val newDeclaredTypes = declaredTypes.asSequence()
                // when exiting, we need to use fully qualified name.
                .filter { (name, _) -> name != clazz.name && !name.contains(other = ".") }
                .fold(initial = declaredTypes) { dec, (name, genericsInfo) ->
                    dec.remove(key = name).put(key = "${clazz.name}.$name", value = genericsInfo)
                }
        // remove private members
        val removeAndChangeMember = { env: FpMap<String, TypeInfo>, member: ClassMember ->
            val name = member.name
            if (member.isPublic) {
                val v = env[name] ?: error(message = "Impossible")
                env.remove(key = name).put(key = "${clazz.name}.$name", value = v)
            } else {
                env.remove(key = name)
            }
        }
        val newTypeEnv = typeEnv
                .let { m.constantMembers.fold(initial = it, operation = removeAndChangeMember) }
                .let { m.functionMembers.fold(initial = it, operation = removeAndChangeMember) }
        return TypeCheckingEnv(
                typeDefinitions = removedTypeDefinitions,
                declaredTypes = newDeclaredTypes, typeEnv = newTypeEnv
        )
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
                        stringTypeId.name to stringTypeId.genericsInfo
                )
        )
    }

}
