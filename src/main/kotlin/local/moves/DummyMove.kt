package org.example.local.moves

class DummyMove: Move() {

    override val delta: Int = Int.MAX_VALUE

    override fun checkValidity(): Validity {
        return Validity.BROKEN
    }

    override fun execute() {
        throw Exception("Dummy move executed!")
    }

    override fun getSignature(): Pair<Long, Long> {
        throw Exception("Dummy move signature requested!")
    }

    override fun addNextMoves(
        dm: Array<IntArray>,
        LM: MutableList<Move>,
        moveSet: MutableSet<Pair<Long, Long>>,
        cycles: List<MutableList<Int>>
    ) {
        throw Exception("Next move of dummy move requested!")
    }
}