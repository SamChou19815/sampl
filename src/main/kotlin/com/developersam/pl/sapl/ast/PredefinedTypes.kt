package com.developersam.pl.sapl.ast

/**
 * [unitTypeExpr] is the type expression for the unit type.
 */
val unitTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = "Unit")

/**
 * [intTypeExpr] is the type expression for the integer type.
 */
val intTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = "Int")

/**
 * [floatTypeExpr] is the type expression for the float type.
 */
val floatTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = "Float")

/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
val boolTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = "Bool")

/**
 * [charTypeExpr] is the type expression for the char type.
 */
val charTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = "Char")

/**
 * [stringTypeExpr] is the type expression for the string type.
 */
val stringTypeExpr: TypeExpr.Identifier = TypeExpr.Identifier(type = "String")

/**
 * [allPredefinedTypeExpr] contains an array of all predifined type expressions.
 */
val allPredefinedTypeExpr: Array<TypeExpr.Identifier> = arrayOf(
        unitTypeExpr, intTypeExpr, floatTypeExpr, boolTypeExpr, charTypeExpr, stringTypeExpr)
