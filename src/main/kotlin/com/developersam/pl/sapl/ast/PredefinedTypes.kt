package com.developersam.pl.sapl.ast


/*
 * -------------------------------------------------------------------------------
 * Part 1: Type Identifiers
 * -------------------------------------------------------------------------------
 */

/**
 * [unitTypeIdentifier] is the type identifier for the unit type.
 */
private val unitTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Unit")
/**
 * [intTypeIdentifier] is the type identifier for the integer type.
 */
private val intTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Int")
/**
 * [floatTypeIdentifier] is the type identifier for the float type.
 */
private val floatTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Float")
/**
 * [boolTypeIdentifier] is the type identifier for the bool type.
 */
private val boolTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Bool")
/**
 * [charTypeIdentifier] is the type identifier for the char type.
 */
private val charTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "Char")
/**
 * [stringTypeIdentifier] is the type identifier for the string type.
 */
private val stringTypeIdentifier: TypeIdentifier = TypeIdentifier(type = "String")

/*
 * -------------------------------------------------------------------------------
 * Part 2: Type Expressions
 * -------------------------------------------------------------------------------
 */

/**
 * [unitTypeExpr] is the type expression for the unit type.
 */
private val unitTypeExpr: TypeExprInAnnotation =
        SingleIdentifierTypeInAnnotation(identifier = unitTypeIdentifier)
/**
 * [intTypeExpr] is the type expression for the integer type.
 */
private val intTypeExpr: TypeExprInAnnotation =
        SingleIdentifierTypeInAnnotation(identifier = intTypeIdentifier)
/**
 * [floatTypeExpr] is the type expression for the float type.
 */
private val floatTypeExpr: TypeExprInAnnotation =
        SingleIdentifierTypeInAnnotation(identifier = floatTypeIdentifier)
/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
private val boolTypeExpr: TypeExprInAnnotation =
        SingleIdentifierTypeInAnnotation(identifier = boolTypeIdentifier)
/**
 * [charTypeExpr] is the type expression for the char type.
 */
private val charTypeExpr: TypeExprInAnnotation =
        SingleIdentifierTypeInAnnotation(identifier = charTypeIdentifier)
/**
 * [stringTypeExpr] is the type expression for the string type.
 */
private val stringTypeExpr: TypeExprInAnnotation =
        SingleIdentifierTypeInAnnotation(identifier = stringTypeIdentifier)

/*
 * -------------------------------------------------------------------------------
 * Part 3: Type Information
 * -------------------------------------------------------------------------------
 */

/**
 * [unitTypeInfo] is the type information for the unit type.
 */
internal val unitTypeInfo: TypeInformation = TypeInformation(typeExpr = unitTypeExpr)
/**
 * [intTypeInfo] is the type information for the integer type.
 */
internal val intTypeInfo: TypeInformation = TypeInformation(typeExpr = intTypeExpr)
/**
 * [floatTypeInfo] is the type information for the float type.
 */
internal val floatTypeInfo: TypeInformation = TypeInformation(typeExpr = floatTypeExpr)
/**
 * [boolTypeInfo] is the type information for the bool type.
 */
internal val boolTypeInfo: TypeInformation = TypeInformation(typeExpr = boolTypeExpr)
/**
 * [charTypeInfo] is the type information for the char type.
 */
internal val charTypeInfo: TypeInformation = TypeInformation(typeExpr = charTypeExpr)
/**
 * [stringTypeInfo] is the type information for the string type.
 */
internal val stringTypeInfo: TypeInformation = TypeInformation(typeExpr = stringTypeExpr)
