package com.developersam.pl.sapl.ast

/**
 * [PredefinedTypes] contains a set of predefined types related AST nodes.
 */
object PredefinedTypes {

    /**
     * [unitTypeIdentifier] is the type identifier for the unit type.
     */
    val unitTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Unit")
    /**
     * [intTypeIdentifier] is the type identifier for the integer type.
     */
    val intTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Int")
    /**
     * [intTypeIdentifier] is the type identifier for the integer type.
     */
    val floatTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Float")
    /**
     * [intTypeIdentifier] is the type identifier for the integer type.
     */
    val boolTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Bool")
    /**
     * [intTypeIdentifier] is the type identifier for the integer type.
     */
    val charTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Char")
    /**
     * [intTypeIdentifier] is the type identifier for the integer type.
     */
    val stringTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "String")

    /**
     * [annotatedUnit] is the annotated variable for unit.
     */
    val annotatedUnit = Pair<String, TypeExprInAnnotation>(
            first = "___ILLEGAL_IDENTIFIER___",
            second = SingleIdentifierTypeInAnnotation(identifier = unitTypeIdentifier)
    )

}