package com.developersam.pl.sapl.environment

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.TypeDeclaration
import com.developersam.pl.sapl.ast.TypeInfo
import com.developersam.pl.sapl.ast.boolTypeId
import com.developersam.pl.sapl.ast.charTypeId
import com.developersam.pl.sapl.ast.floatTypeId
import com.developersam.pl.sapl.ast.intTypeId
import com.developersam.pl.sapl.ast.raw.Module
import com.developersam.pl.sapl.ast.raw.ModuleMember
import com.developersam.pl.sapl.ast.stringTypeId
import com.developersam.pl.sapl.ast.unitTypeId

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
    fun enterModule(module: Module): TypeCheckingEnv {
        val typeMembers = module.members.typeMembers
        return TypeCheckingEnv(
                typeDefinitions = typeMembers.fold(typeDefinitions) { acc, m ->
                    val id = m.identifier
                    acc.put(key = id.name, value = id.genericsInfo to m.declaration)
                },
                declaredTypes = typeMembers.fold(declaredTypes) { acc, m ->
                    val id = m.identifier
                    acc.put(key = id.name, value = id.genericsInfo)
                },
                typeEnv = typeEnv
        )
    }

    /**
     * [exitModule] produces a new [TypeCheckingEnv] with all the public information preserved and
     * make the access of current module elements prefixed with module name.
     */
    fun exitModule(module: Module): TypeCheckingEnv {
        val m = module.members
        // remove added type definitions
        val removedCurrentLevelTypeDefinitions = m.typeMembers.fold(typeDefinitions) { e, member ->
            e.remove(key = member.identifier.name)
        }
        // remove and change declared types
        val newDeclaredTypes = m.typeMembers.fold(initial = declaredTypes) { dec, member ->
            val id = member.identifier
            val name = id.name
            if (member.isPublic) {
                dec.remove(key = name).put(key = name, value = id.genericsInfo)
            } else {
                dec.remove(key = id.name)
            }
        }
        // remove private members
        val removeAndChangeMember = { env: FpMap<String, TypeInfo>, member: ModuleMember ->
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
                typeDefinitions = removedCurrentLevelTypeDefinitions,
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
