package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.TypeExpr
import com.developersam.pl.sapl.ast.raw.ModuleMember

/**
 * [DecoratedModuleMember] defines the operation that all module member must support.
 */
interface DecoratedModuleMember : ModuleMember {

    /**
     * [type] is the type of the module member.
     */
    val type: TypeExpr

}
