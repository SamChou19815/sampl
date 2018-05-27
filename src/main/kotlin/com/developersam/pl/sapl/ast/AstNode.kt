package com.developersam.pl.sapl.ast

/**
 * [AstNode] is a node in the AST.
 * It defines a set of operations that the AST must support.
 */
internal interface AstNode {

    /**
     * [accept] accepts the evaluation from the given [visitor] and returns the evaluation result
     * from [visitor].
     *
     * @param T type of the return value from visitor.
     */
    fun <T> accept(visitor: AstVisitor<T>): T

}
