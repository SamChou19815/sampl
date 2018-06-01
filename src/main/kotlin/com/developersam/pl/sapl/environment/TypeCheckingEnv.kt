package com.developersam.pl.sapl.environment

import com.developersam.fp.FpMap
import com.developersam.fp.FpSet
import com.developersam.pl.sapl.ast.TypeDeclaration
import com.developersam.pl.sapl.ast.TypeExpr
import com.developersam.pl.sapl.ast.TypeInfo
import com.developersam.pl.sapl.ast.allPredefinedTypeExpr
import com.developersam.pl.sapl.ast.raw.Module
import com.developersam.pl.sapl.ast.raw.ModuleMember

/**
 * [TypeCheckingEnv] is the environment for type checking. It contains a set of currently
 * determined definitions to help type check the program.
 *
 * @param typeDefinitions the set that maps type identifiers to actual types.
 * @param declaredTypes the set of declared types with correctly qualified identifiers.
 * @param typeEnv the type environment with correctly qualified identifiers.
 */
data class TypeCheckingEnv(
        val typeDefinitions: FpMap<TypeExpr.Identifier, TypeDeclaration> = FpMap.empty(),
        val declaredTypes: FpSet<TypeExpr.Identifier> = FpSet.empty(),
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
                    acc.put(key = m.identifier, value = m.declaration)
                },
                declaredTypes = typeMembers.fold(declaredTypes) { acc, m ->
                    acc.add(value = m.identifier)
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
            e.remove(key = member.identifier)
        }
        // remove and change declared types
        val newDeclaredTypes = m.typeMembers.fold(initial = declaredTypes) { dec, member ->
            val id = member.identifier
            if (member.isPublic) {
                dec.remove(value = id).add(value = TypeExpr.Identifier(
                        type = "${module.name}.${id.type}", genericsList = id.genericsList
                ))
            } else {
                dec.remove(value = id)
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
                .let { m.typeMembers.fold(initial = it, operation = removeAndChangeMember) }
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
                declaredTypes = FpSet.create(*allPredefinedTypeExpr)
        )
    }

}
