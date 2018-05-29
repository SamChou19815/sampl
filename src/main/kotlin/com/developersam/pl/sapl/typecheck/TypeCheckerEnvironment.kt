package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.TypeExprInAnnotation
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.ast.TypeInformation

/**
 * [Def] is the collection of definition for type checking.
 */
private typealias Def = FpMap<TypeIdentifier, TypeExprInDeclaration>

/**
 * [Env] is the environment for type checking
 */
private typealias Env = FpMap<String, TypeInformation>

internal data class TypeCheckerEnvironment(
        val currentModuleTracker: CurrentModuleTracker,
        val currentLevelTypeDefinitions: Def,
        val upperLevelTypeEnvironment: Env,
        val currentLevelTypeEnvironment: Env
) {

    /**
     * [getTypeInformation] returns the optionally existing type information for the given
     * [variable], with potentially fully-qualified name.
     */
    fun getTypeInformation(variable: String): TypeInformation? =
            currentLevelTypeEnvironment[variable] ?: upperLevelTypeEnvironment[variable]

    fun enterSubModule(subModuleName: String): TypeCheckerEnvironment {
        TODO()
    }

    fun leaveSubModule(): TypeCheckerEnvironment {
        TODO()
    }

}
