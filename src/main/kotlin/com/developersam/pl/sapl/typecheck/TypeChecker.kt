package com.developersam.pl.sapl.typecheck

import com.developersam.fp.FpMap
import com.developersam.pl.sapl.ast.Module

/**
 * [TypeChecker] defines how a type checker should work.
 */
internal object TypeChecker {

    /**
     * [typeCheck] tries to type check the given [module].
     *
     * It should run without error if the [module] type checks. Otherwise, it should throw an
     * unchecked exception.
     */
    fun typeCheck(module: Module) {
        val environment = TypeCheckerEnvironment(
                currentModuleTracker = CurrentModuleTracker(module.name),
                upperLevelTypeDefinitions = FpMap.empty(),
                currentLevelTypeDefinitions = FpMap.empty(),
                upperLevelTypeEnvironment = FpMap.empty(),
                currentLevelTypeEnvironment = FpMap.empty()
        )
        TypeCheckerVisitor(environment = environment).visit(module = module)
    }

}
