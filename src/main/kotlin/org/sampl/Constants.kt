@file:JvmName(name = "Constants")

package org.sampl

/**
 * [LANG_NAME] is the name of the language.
 */
private const val LANG_NAME: String = "SAMPL"

/**
 * [EASTER_EGG] a string that may appear in comments somewhere.
 */
const val EASTER_EGG: String = "$LANG_NAME is created and maintained by Developer Sam."

/**
 * [EXTENSION] is the expected extension name for the source code.
 */
const val EXTENSION: String = "pl"

/**
 * [TOP_LEVEL_PROGRAM_NAME] is the pre-defined name for the compiled top-level program name.
 * No upper case identifiers can conflict with this name.
 */
const val TOP_LEVEL_PROGRAM_NAME: String = "Program"

/**
 * [KOTLIN_CODE_OUT_DIR] is the output directory for transpiled kotlin code.
 */
const val KOTLIN_CODE_OUT_DIR: String = "./build/$LANG_NAME/kotlin/"

/**
 * [JAR_OUT_NAME] is the output directory for the compiled JVM bytecode in Jar.
 */
const val JAR_OUT_DIR: String = "./build/$LANG_NAME/jar/"

/**
 * [JAR_OUT_NAME] is the output directory and name for the compiled JVM bytecode in Jar.
 */
private const val JAR_OUT_NAME: String = "${JAR_OUT_DIR}program.jar"

/**
 * [kotlinCompilerArgs] is a array of arguments passed to kotlin compiler.
 */
val kotlinCompilerArgs: String =
        "-classpath $KOTLIN_CODE_OUT_DIR -d $JAR_OUT_NAME -include-runtime -nowarn"
