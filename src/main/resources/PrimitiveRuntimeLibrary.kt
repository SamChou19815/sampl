class PLException(val m: String) : RuntimeException(m)
fun printInt(value: Long): Unit = print(value)
fun printFloat(value: Double): Unit = print(value)
fun printBool(value: Boolean): Unit = print(value)
fun printChar(value: Char): Unit = print(value)
fun printString(value: String): Unit = print(value)
fun <T : Any> printObject(value: T): Unit = println(value)
fun println(): Unit = println()
fun printlnInt(value: Long): Unit = println(value)
fun printlnFloat(value: Double): Unit = println(value)
fun printlnBool(value: Boolean): Unit = println(value)
fun printlnChar(value: Char): Unit = println(value)
fun printlnString(value: String): Unit = println(value)
fun <T : Any> printlnObject(value: T): Unit = println(value)
fun readLine(): String = kotlin.io.readLine()!!
fun floatToInt(value: Double): Long = value.toLong()
fun stringToInt(value: String): Long =
        value.toLongOrNull() ?: throw PLException("NOT_CONVERTIBLE")
fun intToFloat(value: Long): Double = value.toDouble()
fun stringToFloat(value: String): Double =
        value.toDoubleOrNull() ?: throw PLException("NOT_CONVERTIBLE")
fun intToString(value: Long): String = value.toString()
fun floatToString(value: Double): String = value.toString()
fun boolToString(value: Boolean): String = value.toString()
fun charToString(value: Char): String = value.toString()
fun <T : Any> objectToString(value: T): String = value.toString()
fun getLength(s: String): Long = s.length.toLong()
fun getChar(index: Long, s: String): Char =
        try {
            s[index.toInt()]
        } catch (e: IndexOutOfBoundsException) {
            throw throw PLException("OUT_OF_BOUND")
        }
fun getSubstring(from: Long, to: Long, s: String): String =
        try {
            s.substring(from.toInt(), to.toInt())
        } catch (e: IndexOutOfBoundsException) {
            throw throw PLException("OUT_OF_BOUND")
        }
fun trimString(s: String): String = s.trim()
fun containsSubstring(sub: String, s: String): Boolean = s.contains(other = sub)
fun indexOf(sub: String, s: String): Long = s.indexOf(string = sub).toLong()
