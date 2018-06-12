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

/**
 * [stringArrayTypeName] is the name of the string array type.
 */
const val stringArrayTypeName: String = "StringArray"

// Part 2: Type Identifiers

/**
 * [unitTypeId] is the type identifier for the unit type.
 */
val unitTypeId: TypeIdentifier = TypeIdentifier(name = unitTypeName)

/**
 * [intTypeId] is the type identifier for the int type.
 */
val intTypeId: TypeIdentifier = TypeIdentifier(name = intTypeName)

/**
 * [floatTypeId] is the type identifier for the float type.
 */
val floatTypeId: TypeIdentifier = TypeIdentifier(name = floatTypeName)

/**
 * [boolTypeId] is the type identifier for the bool type.
 */
val boolTypeId: TypeIdentifier = TypeIdentifier(name = boolTypeName)

/**
 * [charTypeId] is the type identifier for the char type.
 */
val charTypeId: TypeIdentifier = TypeIdentifier(name = charTypeName)

/**
 * [stringTypeId] is the type identifier for the string type.
 */
val stringTypeId: TypeIdentifier = TypeIdentifier(name = stringTypeName)

/**
 * [stringArrayTypeId] is the type identifier for the string array type.
 */
val stringArrayTypeId: TypeIdentifier = TypeIdentifier(name = stringArrayTypeName)

// Part 3: Type Expressions

/**
 * [unitTypeExpr] is the type expression for the unit type.
 */
val unitTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = unitTypeName)

/**
 * [intTypeExpr] is the type expression for the integer type.
 */
val intTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = intTypeName)

/**
 * [floatTypeExpr] is the type expression for the float type.
 */
val floatTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = floatTypeName)

/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
val boolTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = boolTypeName)

/**
 * [charTypeExpr] is the type expression for the char type.
 */
val charTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = charTypeName)

/**
 * [stringTypeExpr] is the type expression for the string type.
 */
val stringTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = stringTypeName)

/**
 * [stringArrayTypeExpr] is the type expression for the string array type.
 */
val stringArrayTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = stringArrayTypeName)
