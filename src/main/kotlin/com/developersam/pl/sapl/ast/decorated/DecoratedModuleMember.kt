package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.raw.ModuleMember
import com.developersam.pl.sapl.ast.type.TypeExpr

/**
 * [DecoratedModuleMember] defines the operation that all module member must support.
 */
interface DecoratedModuleMember : ModuleMember, PrettyPrintable {

    /**
     * [type] is the type of the module member.
     */
    val type: TypeExpr

}
