package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.TypeIdentifier
import com.developersam.pl.sapl.environment.Environment

internal data class TypeCheckerEnvironment(
        val currentModuleTracker: CurrentModuleTracker,
        val typesEnvironment: Environment<TypeIdentifier>
) {

    fun enterSubModule(subModuleName: String): TypeCheckerEnvironment {
        TODO()
    }

    fun leaveSubModule(): TypeCheckerEnvironment {
        TODO()
    }

}
