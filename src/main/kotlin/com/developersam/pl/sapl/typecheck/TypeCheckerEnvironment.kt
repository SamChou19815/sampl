package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.TypeExprInDeclaration
import com.developersam.pl.sapl.ast.TypeIdentifier

internal data class TypeCheckerEnvironment(
        val currentModuleTracker: CurrentModuleTracker,
        val upperLevelTypeDefinitions: FpMap<TypeIdentifier, TypeExprInDeclaration>,
        val currentLevelTypeDefinitions: FpMap<TypeIdentifier, TypeExprInDeclaration>,
        val upperLevelTypeEnvironment: FpMap<String, TypeIdentifier>,
        val currentLevelTypeEnvironment: FpMap<String, TypeIdentifier>
) {

    fun enterSubModule(subModuleName: String): TypeCheckerEnvironment {
        TODO()
    }

    fun leaveSubModule(): TypeCheckerEnvironment {
        TODO()
    }

}
