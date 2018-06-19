package org.sampl.exceptions

/**
 * [VariantNotFoundError] reports that a variant with typeName.variantName is not found in current
 * scope.
 */
class VariantNotFoundError(lineNo: Int, typeName: String, variantName: String) : CompileTimeError(
        reason = "The given variant $typeName.$variantName at line $lineNo is not found."
)

