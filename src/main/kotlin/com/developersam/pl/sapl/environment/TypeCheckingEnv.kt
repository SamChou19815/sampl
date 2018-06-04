package com.developersam.pl.sapl.environment

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.raw.Clazz
import com.developersam.pl.sapl.ast.raw.ClassMember
import com.developersam.pl.sapl.ast.type.TypeDeclaration
import com.developersam.pl.sapl.ast.type.TypeInfo
import com.developersam.pl.sapl.ast.type.boolTypeId
import com.developersam.pl.sapl.ast.type.charTypeId
import com.developersam.pl.sapl.ast.type.floatTypeId
import com.developersam.pl.sapl.ast.type.intTypeId
import com.developersam.pl.sapl.ast.type.stringTypeId
import com.developersam.pl.sapl.ast.type.unitTypeId

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
     * [enterModule] produces a new [TypeCheckingEnv] with all the public information preserved and
     * make all the types declared in the module available. Type checking is not done here.
     */
    fun enterModule(module: Clazz): TypeCheckingEnv {
        return TypeCheckingEnv(
                typeDefinitions = typeDefinitions.put(
                        key = module.name,
                        value = module.identifier.genericsInfo to module.declaration
                ),
                declaredTypes = declaredTypes.put(
                        key = module.name, value = module.identifier.genericsInfo
                ),
                typeEnv = typeEnv
        )
    }

    /**
     * [exitModule] produces a new [TypeCheckingEnv] with all the public information preserved and
     * make the access of current module elements prefixed with module name.
     */
    fun exitModule(module: Clazz): TypeCheckingEnv {
        val m = module.members
        // remove added type definitions
        val removedTypeDefinitions = typeDefinitions.remove(key = module.name)
        // remove and change declared types
        val newDeclaredTypes = declaredTypes.asSequence()
                // when exiting, we need to use fully qualified name.
                .filter { (name, _) -> name != module.name && !name.contains(other = ".") }
                .fold(initial = declaredTypes) { dec, (name, genericsInfo) ->
                    dec.remove(key = name).put(key = "${module.name}.$name", value = genericsInfo)
                }
        // remove private members
        val removeAndChangeMember = { env: FpMap<String, TypeInfo>, member: ClassMember ->
            val name = member.name
            if (member.isPublic) {
                val v = env[name] ?: error(message = "Impossible")
                env.remove(key = name).put(key = "${module.name}.$name", value = v)
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
