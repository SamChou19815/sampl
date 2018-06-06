@file:JvmName(name = "Program")

/*
 * SAMPL is created and maintained by Developer Sam.
 */

class PLException(val m: String): RuntimeException(m)

class TestingProgram (
) {
    fun copy(): TestingProgram =
        TestingProgram()
    
    companion object {
        val trueVar: Unit =
            Unit
        
        val implication: (String) -> Int =
            { a: String ->
                5
            }
        
        fun <A,B> modusPonens(f: (A) -> B, v: A): B =
            f(v)
        
        fun constant5Impl1(v: String): Int =
            implication(v)
        
        fun constant5Impl2(v: String): Int =
            modusPonens<String,Int>(implication, v)
        
        fun applyWithString(_unit_: Unit): Int =
            constant5Impl2("hi")
        
        fun add(a: Int, b: Int): Int =
            (a) + (b)
        
        fun add1(b: Int): Int =
            { _tempV1: Int -> add(1, _tempV1) }(b)
        
        fun main(_unit_: Unit): Unit =
            Unit
        
    class And<A,B> (
        val a: A,
        val b: B
    ) {
        fun copy(a: A = this.a, b: B = this.b): And<A,B> =
            And(a = a, b = b)
        
        companion object {
        }
    }
    
    sealed class Or<A,B> {
        class Second<Nothing,B>(val data: B)
        class First<A,Nothing>(val data: A)
        companion object {
        }
    }
    
    class Empty (
    ) {
        fun copy(): Empty =
            Empty()
        
        companion object {
        }
    }
    
    }
}


fun main(args: Array<String>) {
    TestingProgram.main(_unit_ = Unit)
}
