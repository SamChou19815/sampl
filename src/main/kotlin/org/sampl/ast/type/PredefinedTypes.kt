package org.sampl.ast.type

// Part 1: Type Names

/**
 * [unitTypeName] is the name of the unit type.
 */
internal const val unitTypeName: String = "Unit"

/**
 * [intTypeName] is the name of the int type.
 */
internal const val intTypeName: String = "Int"

/**
 * [floatTypeName] is the name of the float type.
 */
internal const val floatTypeName: String = "Float"

/**
 * [boolTypeName] is the name of the bool type.
 */
internal const val boolTypeName: String = "Bool"

/**
 * [charTypeName] is the name of the char type.
 */
internal const val charTypeName: String = "Char"

/**
 * [stringTypeName] is the name of the string type.
 */
internal const val stringTypeName: String = "String"

/**
 * [stringArrayTypeName] is the name of the string array type.
 */
internal const val stringArrayTypeName: String = "StringArray"

// Part 2: Type Identifiers

/**
 * [unitTypeId] is the type identifier for the unit type.
 */
internal val unitTypeId: TypeIdentifier = TypeIdentifier(name = unitTypeName)

/**
 * [intTypeId] is the type identifier for the int type.
 */
internal val intTypeId: TypeIdentifier = TypeIdentifier(name = intTypeName)

/**
 * [floatTypeId] is the type identifier for the float type.
 */
internal val floatTypeId: TypeIdentifier = TypeIdentifier(name = floatTypeName)

/**
 * [boolTypeId] is the type identifier for the bool type.
 */
internal val boolTypeId: TypeIdentifier = TypeIdentifier(name = boolTypeName)

/**
 * [charTypeId] is the type identifier for the char type.
 */
internal val charTypeId: TypeIdentifier = TypeIdentifier(name = charTypeName)

/**
 * [stringTypeId] is the type identifier for the string type.
 */
internal val stringTypeId: TypeIdentifier = TypeIdentifier(name = stringTypeName)

/**
 * [stringArrayTypeId] is the type identifier for the string array type.
 */
internal val stringArrayTypeId: TypeIdentifier = TypeIdentifier(name = stringArrayTypeName)

// Part 3: Type Expressions

/**
 * [unitTypeExpr] is the type expression for the unit type.
 */
internal val unitTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = unitTypeName)

/**
 * [intTypeExpr] is the type expression for the integer type.
 */
internal val intTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = intTypeName)

/**
 * [floatTypeExpr] is the type expression for the float type.
 */
internal val floatTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = floatTypeName)

/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
internal val boolTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = boolTypeName)

/**
 * [charTypeExpr] is the type expression for the char type.
 */
internal val charTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = charTypeName)

/**
 * [stringTypeExpr] is the type expression for the string type.
 */
internal val stringTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = stringTypeName)

/**
 * [stringArrayTypeExpr] is the type expression for the string array type.
 */
internal val stringArrayTypeExpr: TypeExpr.Identifier =
        TypeExpr.Identifier(type = stringArrayTypeName)
