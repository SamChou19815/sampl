package com.developersam.pl.sapl.ast

/**
 * [AstVisitor] defines a set of visiting function that an AST visitor must all implement.
 *
 * @param T type of the return value.
 */
internal interface AstVisitor<T> {

    /**
     * [visit] visits the given [module] and returns the evaluation result.
     */
    fun visit(module: Module): T

    /**
     * [visit] visits the given [expression] and returns the evaluation result.
     */
    fun visit(expression: Expression): T

}
