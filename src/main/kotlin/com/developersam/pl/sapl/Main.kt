@file:JvmName(name = "Main")

package com.developersam.pl.sapl

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Bad Usage!")
    }
    val directory = args[0]
    val compiler = LanguageCompiler(directory = directory)
    compiler.compile()
}
