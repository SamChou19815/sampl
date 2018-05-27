package com.developersam.pl.sapl.typecheck

import com.developersam.pl.sapl.ast.Module

/**
 * [TypeChecker] is defines how a type checker should work.
 */
interface TypeChecker {

    /**
     * [doesTypeCheck] reports whether the given [module] does type check.
     *
     * It should run without error if the [module] type checks. Otherwise, it should throw an
     * unchecked exception.
     */
    fun doesTypeCheck(module: Module)

}