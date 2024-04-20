package org.example.local

abstract class Move: Comparable<Move> {

    enum class Validity {
        VALID, INVERTED, BROKEN
    }

    abstract val delta: Int

    abstract fun checkValidity(): Validity
    abstract fun execute()

    override fun compareTo(other: Move): Int {
        return delta - other.delta
    }

}