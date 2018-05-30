package com.developersam.pl.sapl.ast

import com.developersam.pl.sapl.exceptions.GenericInfoWrongNumberOfArgumentsError

/**
 * [TypeInformation] is the data class that contains both an type expression [typeExpr] and another
 * set [genericInfo] to tell whether any arguments in the type are generic.
 */
internal data class TypeInformation(
        val typeExpr: TypeExprInAnnotation, val genericInfo: List<String> = emptyList()
) {

    /**
     * [assertNoGenericsInfo] asserts that there is no generic info in this type information.
     * If there is, it will throw [GenericInfoWrongNumberOfArgumentsError].
     */
    fun assertNoGenericsInfo() {
        if (genericInfo.isNotEmpty()) {
            throw GenericInfoWrongNumberOfArgumentsError(actualNumber = genericInfo.size)
        }
    }

    /**
     * [intersect] will try to find a type that is the intersection of this type and the given
     * type [another]. If no such type exists, it will return `null`.
     */
    infix fun intersect(another: TypeInformation): TypeInformation? {
        TODO()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
