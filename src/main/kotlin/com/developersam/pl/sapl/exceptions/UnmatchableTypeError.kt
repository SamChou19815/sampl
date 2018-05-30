package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.TypeExprInAnnotation

/**
 * [UnmatchableTypeError] reports an unmatchable type [typeExpr] in pattern matching.
 */
internal class UnmatchableTypeError(val typeExpr: TypeExprInAnnotation) : CompileTimeError(
        reason = "This type $typeExpr is not matchable!"
)
