package com.developersam.pl.sapl.ast

/**
 * [UnitNode] contains a set of unit related AST nodes.
 */
object UnitNode {

    /**
     * [annotatedUnit] is the annotated variable for unit.
     */
    val annotatedUnit = Pair<String, TypeExprInAnnotation>(
            first = "___ILLEGAL_IDENTIFIER___",
            second = SingleIdentifierTypeInAnnotation(identifier = TypeIdentifier(type = "Unit"))
    )

}