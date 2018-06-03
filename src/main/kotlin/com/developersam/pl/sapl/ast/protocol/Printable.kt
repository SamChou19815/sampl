package com.developersam.pl.sapl.ast.protocol

import com.developersam.pl.sapl.codegen.IndentationQueue
import com.developersam.pl.sapl.config.IndentationStrategy.TWO_SPACES

/**
 * [Printable] specifies how an AST node can be printed.
 */
interface Printable {

    /**
     * [asIndentedSourceCode] returns the source code of the AST node in well formatted way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     * Calling this function assumes that you are at indentation level 0.
     */
    val asIndentedSourceCode: String
        get() = IndentationQueue(strategy = TWO_SPACES)
                .apply { prettyPrint(q = this) }
                .toIndentedCode()

    /**
     * [asInlineSourceCode] returns the source code of the AST node in one-liner way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     * Calling this function assumes that you are at indentation level 0.
     */
    val asInlineSourceCode: String
        get() = IndentationQueue(strategy = TWO_SPACES)
                .apply { prettyPrint(q = this) }
                .toInlineCode()

    /**
     * [asIndentedSourceCode] prints the source code of the AST node in well formatted way.
     * It does not need to worry about the x-character-limit issue, but it does need to consider
     * proper indentation.
     *
     * @param q the queue used to push indentation info.
     */
    fun prettyPrint(q: IndentationQueue)

}
