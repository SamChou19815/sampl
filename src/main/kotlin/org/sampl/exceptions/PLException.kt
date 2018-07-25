package org.sampl.exceptions

/**
 * [PLException] is the only exception used inside this programming language.
 *
 * @param m message given to the user.
 */
class PLException internal constructor(val m: String) : RuntimeException(m)
