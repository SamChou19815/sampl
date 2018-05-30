package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.ast.TypeInformation

/**
 * [TypeEnv] is the environment for type checking
 */
internal typealias TypeEnv = FpMap<String, TypeInformation>

/**
 * [TypeCheckerEnv] is the environment for type checking. It contains a set of currently determined
 * definitions to help type check the program.
 *
 * @param
 */
internal data class TypeCheckerEnv(
        val typeDefinitions: FpMap<TypeIdentifier, TypeExprInDeclaration>,
        val upperLevelTypeEnv: TypeEnv, val currentLevelTypeEnv: TypeEnv
) {

    /**
     * [updateCurrent] creates a new [TypeCheckerEnv] with current level type environment updated
     * to [newValue].
     */
    fun updateCurrent(newValue: TypeEnv): TypeCheckerEnv = copy(currentLevelTypeEnv = newValue)

    /**
     * [getTypeInfo] returns the optionally existing type information for the given
     * [variable], with potentially fully-qualified name.
     */
    fun getTypeInfo(variable: String): TypeInformation? =
            currentLevelTypeEnv[variable] ?: upperLevelTypeEnv[variable]

    /**
     * [updateTypeInfo] creates a new [TypeCheckerEnv] that has the current level
     * type environment updated with a new pair [variable] to [typeInfo].
     */
    fun updateTypeInfo(variable: String, typeInfo: TypeInformation): TypeCheckerEnv =
            updateCurrent(newValue = currentLevelTypeEnv.put(variable, typeInfo))

    /**
     * [removeTypeInfo] creates a new [TypeCheckerEnv] that has the current level
     * type environment updated with [variable]'s type information removed.
     */
    fun removeTypeInfo(variable: String): TypeCheckerEnv =
            copy(currentLevelTypeEnv = currentLevelTypeEnv.remove(variable))

}
