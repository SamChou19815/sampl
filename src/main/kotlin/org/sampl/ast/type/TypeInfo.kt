package org.sampl.ast.type

/**
 * [TypeInfo] is the data class that contains both an type expression [typeExpr] and another
 * set [genericsInfo] to tell whether any arguments in the type are generic.
 *
 * @property typeExpr the expression of type.
 * @property genericsInfo a list of generics declared associated with the type, if any.
 */
internal data class TypeInfo(val typeExpr: TypeExpr, val genericsInfo: List<String> = emptyList())
