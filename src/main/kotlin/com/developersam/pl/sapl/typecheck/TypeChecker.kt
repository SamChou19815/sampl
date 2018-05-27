package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.Module
import com.developersam.pl.sapl.environment.FunctionalEnvironment

/**
 * [TypeChecker] defines how a type checker should work.
 */
object TypeChecker {

    /**
     * [typeCheck] tries to type check the given [module].
     *
     * It should run without error if the [module] type checks. Otherwise, it should throw an
     * unchecked exception.
     */
    fun typeCheck(module: Module) {

        val environment = TypeCheckerEnvironment(
                currentModuleTracker = CurrentModuleTracker(module.name),
                typesEnvironment = FunctionalEnvironment.getEmpty()
        )
        TypeCheckerVisitor(environment = environment).visit(module = module)
    }

}
