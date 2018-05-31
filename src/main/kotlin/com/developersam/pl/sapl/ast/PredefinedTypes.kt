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
internal val unitTypeExpr: TypeExprInAnnotation =
        TypeExprInAnnotation.SingleIdentifier(identifier = unitTypeIdentifier)
/**
 * [intTypeExpr] is the type expression for the integer type.
 */
internal val intTypeExpr: TypeExprInAnnotation =
        TypeExprInAnnotation.SingleIdentifier(identifier = intTypeIdentifier)
/**
 * [floatTypeExpr] is the type expression for the float type.
 */
internal val floatTypeExpr: TypeExprInAnnotation =
        TypeExprInAnnotation.SingleIdentifier(identifier = floatTypeIdentifier)
/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
internal val boolTypeExpr: TypeExprInAnnotation =
        TypeExprInAnnotation.SingleIdentifier(identifier = boolTypeIdentifier)
/**
 * [charTypeExpr] is the type expression for the char type.
 */
internal val charTypeExpr: TypeExprInAnnotation =
        TypeExprInAnnotation.SingleIdentifier(identifier = charTypeIdentifier)
/**
 * [stringTypeExpr] is the type expression for the string type.
 */
internal val stringTypeExpr: TypeExprInAnnotation =
        TypeExprInAnnotation.SingleIdentifier(identifier = stringTypeIdentifier)
