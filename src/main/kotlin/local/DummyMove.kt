package org.example.local

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
}