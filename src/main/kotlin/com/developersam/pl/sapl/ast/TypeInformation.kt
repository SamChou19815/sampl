package com.developersam.pl.sapl.ast

/**
 * [TypeInformation] is the data class that contains both an type expression [typeExpr] and another
 * set [genericInfo] to tell whether any arguments in the type are generic.
 */
internal data class TypeInformation(
        val typeExpr: TypeExprInAnnotation, val genericInfo: Set<String> = emptySet()
)
