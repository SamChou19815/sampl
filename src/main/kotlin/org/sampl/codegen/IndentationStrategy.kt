package org.sampl.codegen

/**
 * [IndentationStrategy] contains a collections of supported indentation strategy.
 *
 * @param indentationString the corresponding string used for indentation.
 */
enum class IndentationStrategy(private val indentationString: String) {

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

    companion object {

        /**
         * [indent] adds indentation (2 spaces) to the [builder] as required.
         *
         * @param level the expected indentation level.
         * @return the updated [builder], used for fluent access.
         */
        fun indent2(level: Int, builder: StringBuilder): StringBuilder =
                TWO_SPACES.indent(level, builder)

        /**
         * [indent] adds indentation (4 spaces) to the [builder] as required.
         *
         * @param level the expected indentation level.
         * @return the updated [builder], used for fluent access.
         */
        fun indent4(level: Int, builder: StringBuilder): StringBuilder =
                TWO_SPACES.indent(level, builder)

    }

}
