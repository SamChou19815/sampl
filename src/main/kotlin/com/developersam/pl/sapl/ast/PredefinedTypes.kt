package com.developersam.pl.sapl.ast

/**
 * [unitTypeExpr] is the type expression for the unit type.
 */
val unitTypeExpr: TypeExpr = TypeExpr.Identifier(type = "Unit")

/**
 * [intTypeExpr] is the type expression for the integer type.
 */
val intTypeExpr: TypeExpr = TypeExpr.Identifier(type = "Int")

/**
 * [floatTypeExpr] is the type expression for the float type.
 */
val floatTypeExpr: TypeExpr = TypeExpr.Identifier(type = "Float")

/**
 * [boolTypeExpr] is the type expression for the bool type.
 */
val boolTypeExpr: TypeExpr = TypeExpr.Identifier(type = "Bool")

/**
 * [charTypeExpr] is the type expression for the char type.
 */
val charTypeExpr: TypeExpr = TypeExpr.Identifier(type = "Char")

/**
 * [stringTypeExpr] is the type expression for the string type.
 */
val stringTypeExpr: TypeExpr = TypeExpr.Identifier(type = "String")
