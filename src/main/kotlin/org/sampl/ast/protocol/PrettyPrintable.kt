package org.sampl.ast.protocol

import org.sampl.codegen.IdtQueue
import org.sampl.codegen.IdtStrategy.TWO_SPACES

/**
 * [PrettyPrintable] specifies how an AST node can be pretty-printed.
 */
interface PrettyPrintable {

    /**
     * [asIndentedSourceCode] returns the source code of the AST node in well formatted way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     * Calling this function assumes that you are at indentation level 0.
     */
    val asIndentedSourceCode: String
        get() = IdtQueue(strategy = TWO_SPACES)
                .apply { prettyPrint(q = this) }
                .toIndentedCode()

    /**
     * [asOneLineSourceCode] returns the source code of the AST node in one-liner way.
     */
    val asOneLineSourceCode: String
        get() = IdtQueue(strategy = TWO_SPACES)
                .apply { prettyPrint(q = this) }
                .toOneLineCode()

    /**
     * [asIndentedSourceCode] prints the source code of the AST node in well formatted way to [q].
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     *
     * @param q the queue used to push indentation info.
     */
    fun prettyPrint(q: IdtQueue)

}
