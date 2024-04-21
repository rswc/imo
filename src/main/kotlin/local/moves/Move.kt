package org.example.local.moves

abstract class Move: Comparable<Move> {

    enum class Validity {
        VALID, INVERTED, BROKEN
    }

    abstract val delta: Int

    abstract fun checkValidity(): Validity
    abstract fun execute()
    abstract fun getSignature(): Pair<Long, Long>

    override fun compareTo(other: Move): Int {
        return delta - other.delta
    }

}