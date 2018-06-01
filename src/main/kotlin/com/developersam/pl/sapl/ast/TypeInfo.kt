package com.developersam.pl.sapl.ast

/**
 * [TypeInformation] is the data class that contains both an type expression [typeExpr] and another
 * set [genericInfo] to tell whether any arguments in the type are generic.
 */
data class TypeInformation(
        val typeExpr: TypeExpr, val genericInfo: List<String> = emptyList()
)
