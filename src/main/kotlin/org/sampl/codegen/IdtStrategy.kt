package org.sampl.codegen

/**
 * [IdtStrategy] contains a collections of supported indentation strategy.
 *
 * @param indentationString the corresponding string used for indentation.
 */
internal enum class IdtStrategy(private val indentationString: String) {

    /**
     * Use two spaces for indentation.
     */
    TWO_SPACES(indentationString = "  "),
    /**
     * Use four spaces for indentation.
     */
    FOUR_SPACES(indentationString = "    ");

    /**
     * [indent] adds indentation to the [builder] as required.
     *
     * @param level the expected indentation level.
     * @return the updated [builder], used for fluent access.
     */
    fun indent(level: Int, builder: StringBuilder): StringBuilder {
        repeat(times = level) { builder.append(indentationString) }
        return builder
    }

}
