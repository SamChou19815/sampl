package com.developersam.pl.sapl.environment

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.ast.TypeInformation

/**
 * [TypeCheckingEnv] is the environment for type checking. It contains a set of currently
 * determined definitions to help type check the program.
 *
 * @param typeDefinitions the set that maps type identifiers to actual types.
 * @param upperLevelTypeEnv the upper level type environment. Variables not defined in this module
 * should be here.
 * @param currentLevelTypeEnv the current level type environment.
 */
internal data class TypeCheckingEnv(
        val typeDefinitions: FpMap<TypeIdentifier, TypeExprInDeclaration> = FpMap.empty(),
        val upperLevelTypeEnv: FpMap<String, TypeInformation> = FpMap.empty(),
        val currentLevelTypeEnv: FpMap<String, TypeInformation> = FpMap.empty()
) {

    /**
     * [update] creates a new [TypeCheckingEnv] with current level type environment updated
     * to [newCurrent].
     */
    fun update(newCurrent: FpMap<String, TypeInformation>): TypeCheckingEnv =
            copy(currentLevelTypeEnv = newCurrent)

    /**
     * [get] returns the optionally existing type information for the given
     * [variable], with potentially fully-qualified name.
     */
    operator fun get(variable: String): TypeInformation? =
            currentLevelTypeEnv[variable] ?: upperLevelTypeEnv[variable]

    /**
     * [put] creates a new [TypeCheckingEnv] that has the current level
     * type environment updated with a new pair [variable] to [typeInfo].
     */
    fun put(variable: String, typeInfo: TypeInformation): TypeCheckingEnv =
            update(newCurrent = currentLevelTypeEnv.put(variable, typeInfo))

    /**
     * [remove] creates a new [TypeCheckingEnv] that has the current level
     * type environment updated with [variable]'s type information removed.
     */
    fun remove(variable: String): TypeCheckingEnv =
            update(newCurrent = currentLevelTypeEnv.remove(variable))

    companion object {
        /**
         * [empty] is the empty [TypeCheckingEnv].
         */
        val empty: TypeCheckingEnv = TypeCheckingEnv()
    }

}
