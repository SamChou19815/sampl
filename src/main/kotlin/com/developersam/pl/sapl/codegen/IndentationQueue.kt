package com.developersam.pl.sapl.codegen

import com.developersam.pl.sapl.config.IndentationStrategy
import java.util.LinkedList
import java.util.Queue

/**
 * [IndentationQueue] is a mutable data structure that stores a queue of indentation related
 * information.
 * It is intended usage is: push indentation info into the queue during AST visiting phase, and then
 * iterate through the queue to provide the indented code.
 *
 * @param strategy the strategy for indentation.
 */
class IndentationQueue(private val strategy: IndentationStrategy) {

    /**
     * [Element] represents an element that can appear in the Indentation Queue.
     */
    private sealed class Element {

        /**
         * [Further] means pushing one level further for indentation.
         */
        object Further : Element()

        /**
         * [Back] means pulling one level back for indentation.
         */
        object Back : Element()

        /**
         * [Line] represents a code line in the queue.
         *
         * @property line one line of code.
         */
        data class Line(val line: String) : Element()

    }

    /**
     * [queue] is the queue used internally to represent the stack.
     */
    private val queue: Queue<Element> = LinkedList()

    /**
     * [indentAndApply] indents and lets [action] does some indentation or adding lines in the
     * inner indentation level.
     */
    fun indentAndApply(action: IndentationQueue.() -> Unit) {
        queue.add(Element.Further)
        action.invoke(this)
        queue.add(Element.Back)
    }

    /**
     * [addLine] adds one empty line of code.
     */
    fun addEmptyLine() {
        queue.add(Element.Line(line = ""))
    }

    /**
     * [addLine] adds one [line] of code.
     */
    fun addLine(line: String) {
        queue.add(Element.Line(line = line))
    }

    /**
     * [toIndentedCode] uses all the available information in the queue to generate well indented
     * code.
     *
     * @throws IllegalStateException if the indentation right now is not well-balanced.
     */
    fun toIndentedCode(): String {
        var level = 0
        val builder = StringBuilder()
        for (element in queue) {
            when (element) {
                is Element.Further -> level++
                is Element.Back -> if (--level < 0) throw IllegalStateException()
                is Element.Line -> strategy.indent(level, builder)
                        .append(element.line)
                        .append('\n')
            }
        }
        if (level != 0) {
            throw IllegalStateException()
        }
        return builder.toString()
    }

    /**
     * [toIndentedCode] uses all the available information in the queue to generate a code that only
     * has one line.
     * This function does not care about the well-balancing of indentation.
     * It will simply ignore them.
     */
    fun toInlineCode(): String = queue.asSequence()
            .filter { it is Element.Line }
            .map { (it as Element.Line).line.trim() }
            .joinToString(separator = " ")

}
