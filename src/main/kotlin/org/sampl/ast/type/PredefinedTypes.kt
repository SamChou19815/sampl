package org.sampl.ast.type

// Part 1: Type Names

/**
 * [unitTypeName] is the name of the unit type.
 */
const val unitTypeName: String = "Unit"

/**
 * [intTypeName] is the name of the int type.
 */
const val intTypeName: String = "Int"

/**
 * [floatTypeName] is the name of the float type.
 */
const val floatTypeName: String = "Float"

/**
 * [boolTypeName] is the name of the bool type.
 */
const val boolTypeName: String = "Bool"

/**
 * [charTypeName] is the name of the char type.
 */
const val charTypeName: String = "Char"

/**
 * [stringTypeName] is the name of the string type.
 */
const val stringTypeName: String = "String"

// Part 2: Type Identifiers

/**
 * [unitTypeId] is the type identifier for the unit type.
 */
val unitTypeId: TypeIdentifier =
        TypeIdentifier(name = "Unit")

/**
 * [intTypeId] is the type identifier for the int type.
 */
val intTypeId: TypeIdentifier =
        TypeIdentifier(name = "Int")

/**
 * [floatTypeId] is the type identifier for the float type.
 */
val floatTypeId: TypeIdentifier =
        TypeIdentifier(name = "Float")

/**
 * [boolTypeId] is the type identifier for the bool type.
 */
val boolTypeId: TypeIdentifier =
        TypeIdentifier(name = "Bool")

/**
 * [charTypeId] is the type identifier for the char type.
 */
val charTypeId: TypeIdentifier =
        TypeIdentifier(name = "Char")

/**
 * [stringTypeId] is the type identifier for the string type.
 */
val stringTypeId: TypeIdentifier =
        TypeIdentifier(name = "String")

// Part 3: Type Expressions

/**
 * [unitTypeExpr] is the type expression for the unit type.
 */
val unitTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = "Unit")

/**
 * [intTypeExpr] is the type expression for the integer type.
 */
val intTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = "Int")

/**
 * [floatTypeExpr] is the type expression for the float type.
 */
val floatTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = "Float")

/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
val boolTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = "Bool")

/**
 * [charTypeExpr] is the type expression for the char type.
 */
val charTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = "Char")

/**
 * [stringTypeExpr] is the type expression for the string type.
 */
val stringTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = "String")
