package org.sampl.ast.common

/**
 * [FunctionCategory] is a classification of function's category based on their level of
 * "predefined-ness"
 */
internal enum class FunctionCategory {

    /**
     * [PRIMITIVE] represents the most basic and primitive level functions. These functions are
     * those critical IO functions and type conversion functions. Without these functions, the
     * program in this programming language cannot do anything useful. Functions of this category is
     * defined by this system only, which cannot be modified by the user.
     */
    PRIMITIVE,
    /**
     * [PROVIDED] represents the functions provided by the user of this interpreter/compiler.
     * This is the basic mechanism for Java interop.
     *
     * While this category of functions can be very flexible, it will accompany a performance cost
     * due to the usage of reflection during interpretation.
     */
    PROVIDED,
    /**
     * [USER_DEFINED] represents the functions defined in the actual program. This is the most
     * common category.
     */
    USER_DEFINED

}
