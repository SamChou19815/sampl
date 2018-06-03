package com.developersam.pl.sapl.ast.type

/**
 * [TypeInfo] is the data class that contains both an type expression [typeExpr] and another
 * set [genericsInfo] to tell whether any arguments in the type are generic.
 */
data class TypeInfo(val typeExpr: TypeExpr, val genericsInfo: List<String> = emptyList())
