package com.developersam.fp

sealed class Or<out A, out B> {
    override fun toString(): String =
            when (this) {
                is First<A> -> "[Or.First: $data]"
                is Second<B> -> "[Or.Second: $data]"
            }

    data class First<A>(val data: A) : Or<A, Nothing>()
    data class Second<B>(val data: B): Or<Nothing, B>()
}
