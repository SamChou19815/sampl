package com.developersam.pl.sapl.exceptions

import com.developersam.pl.sapl.ast.TypeExpr

/**
 * [UnmatchableTypeError] reports an unmatchable type [typeExpr] in pattern matching.
 */
class UnmatchableTypeError(val typeExpr: TypeExpr)
    : CompileTimeError(reason = "This type $typeExpr is not matchable!")
