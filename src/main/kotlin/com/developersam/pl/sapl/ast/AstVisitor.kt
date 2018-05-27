package com.developersam.pl.sapl.ast

/**
 * [AstVisitor] defines a set of visiting function that an AST visitor must all implement.
 *
 * @param T type of the return value.
 */
interface AstVisitor<T> {

    /**
     * [visit] visits the given [compilationUnit] and returns the evaluation result.
     *
     * You should never use this method. It is created simply for the niceness of code structure.
     */
    fun visit(compilationUnit: CompilationUnit): T =
            throw UnsupportedOperationException(
                    message = "You are not supposed to visit a compilation unit. " +
                            "This node is reserved for dependency analysis only."
            )

    /**
     * [visit] visits the given [module] and returns the evaluation result.
     */
    fun visit(module: Module): T

    /**
     * [visit] visits the given [moduleMember] and returns the evaluation result.
     */
    fun visit(moduleMember: ModuleMember): T

    /**
     * [visit] visits the given [literal] and returns the evaluation result.
     */
    fun visit(literal: Literal): T

    /**
     * [visit] visits the given [expression] and returns the evaluation result.
     */
    fun visit(expression: Expression): T

    /**
     * [visit] visits the given [pattern] and returns the evaluation result.
     */
    fun visit(pattern: Pattern): T

    /**
     * [visit] visits the given [typeExprInAnnotation] and returns the evaluation result.
     */
    fun visit(typeExprInAnnotation: TypeExprInAnnotation): T

    /**
     * [visit] visits the given [typeExprInDeclaration] and returns the evaluation result.
     */
    fun visit(typeExprInDeclaration: TypeExprInDeclaration): T

}
