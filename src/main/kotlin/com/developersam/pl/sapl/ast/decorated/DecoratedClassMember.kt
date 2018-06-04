package com.developersam.pl.sapl.ast.decorated

import com.developersam.pl.sapl.ast.protocol.PrettyPrintable
import com.developersam.pl.sapl.ast.protocol.Transpilable
import com.developersam.pl.sapl.ast.raw.ClassMember
import com.developersam.pl.sapl.ast.type.TypeExpr

/**
 * [DecoratedClassMember] defines the operation that all decorated class member must support.
 */
interface DecoratedClassMember : ClassMember, PrettyPrintable, Transpilable {

    /**
     * [type] is the type of the class member.
     */
    val type: TypeExpr

}
