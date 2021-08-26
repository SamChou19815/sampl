package com.developersam.fp

class NotFoundError : RuntimeException("NotFound!") {
    companion object {
        fun raise(): Nothing = throw NotFoundError()
    }
}
