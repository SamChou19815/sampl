package org.sampl.ast.decorated

import org.sampl.ast.protocol.PrettyPrintable
import org.sampl.ast.protocol.Transpilable
import org.sampl.ast.raw.ClassMember
import org.sampl.ast.type.TypeExpr

/**
 * [DecoratedClassMember] defines the operation that all decorated class member must support.
 */
interface DecoratedClassMember : ClassMember, PrettyPrintable, Transpilable {

    /**
     * [type] is the type of the class member.
     */
    val type: TypeExpr

}
