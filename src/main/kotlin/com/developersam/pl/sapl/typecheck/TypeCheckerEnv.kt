package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.ast.TypeInformation

/**
 * [TypeCheckerEnv] is the environment for type checking. It contains a set of currently determined
 * definitions to help type check the program.
 *
 * @param
 */
internal data class TypeCheckerEnv(
        val typeDefinitions: FpMap<TypeIdentifier, TypeExprInDeclaration>,
        val upperLevelTypeEnv: FpMap<String, TypeInformation>,
        val currentLevelTypeEnv: FpMap<String, TypeInformation>
) {

    /**
     * [update] creates a new [TypeCheckerEnv] with current level type environment updated
     * to [newCurrent].
     */
    fun update(newCurrent: FpMap<String, TypeInformation>): TypeCheckerEnv =
            copy(currentLevelTypeEnv = newCurrent)

    /**
     * [get] returns the optionally existing type information for the given
     * [variable], with potentially fully-qualified name.
     */
    operator fun get(variable: String): TypeInformation? =
            currentLevelTypeEnv[variable] ?: upperLevelTypeEnv[variable]

    /**
     * [put] creates a new [TypeCheckerEnv] that has the current level
     * type environment updated with a new pair [variable] to [typeInfo].
     */
    fun put(variable: String, typeInfo: TypeInformation): TypeCheckerEnv =
            update(newCurrent = currentLevelTypeEnv.put(variable, typeInfo))

    /**
     * [remove] creates a new [TypeCheckerEnv] that has the current level
     * type environment updated with [variable]'s type information removed.
     */
    fun remove(variable: String): TypeCheckerEnv =
            update(newCurrent = currentLevelTypeEnv.remove(variable))

}
