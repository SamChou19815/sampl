package com.developersam.pl.sapl.ast

/**
 * [PredefinedTypes] contains a set of predefined types related AST nodes.
 */
internal object PredefinedTypes {

    /**
     * [moduleTypeIdentifier] is the type identifier for the module type.
     * It is used only as a type placeholder. This value has no meaning.
     */
    val moduleTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "_ModuleType_")
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

}