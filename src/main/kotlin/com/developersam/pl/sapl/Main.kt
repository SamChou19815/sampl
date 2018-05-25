@file:JvmName(name = "Main")

package com.developersam.pl.sapl

import com.developersam.pl.sapl.console.LanguageCompiler

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Bad Usage!")
    }
    val directory = args[0]
    val compiler = LanguageCompiler(directory = directory)
    compiler.compile()
}
