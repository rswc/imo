package org.example.local.moves

abstract class Move: Comparable<Move> {

    enum class Validity {
        VALID, INVERTED, BROKEN
    }

    abstract val delta: Int

    abstract fun checkValidity(): Validity
    abstract fun execute()
    abstract fun getSignature(): Pair<Long, Long>
    abstract fun addNextMoves(dm: Array<IntArray>, LM: MutableList<Move>, moveSet: MutableSet<Pair<Long, Long>>, cycles: List<MutableList<Int>>)

    override fun compareTo(other: Move): Int {
        if (delta < other.delta) {
            return -1
        } else if (delta > other.delta) {
            return 1
        }

        return 0
    }

}