package org.sampl.exceptions

/**
 * [VariantNotFoundError] reports that a variant [typeName].[variantName] is not found in current
 * scope.
 */
class VariantNotFoundError(val typeName: String, val variantName: String) : CompileTimeError(
        reason = "The given variant $typeName.$variantName is not found."
)

