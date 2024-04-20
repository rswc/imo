package org.example.local

class EdgeMove(
    dm: Array<IntArray>,
    val cycleIndex: Int,
    private val cycle: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
) : Move() {

    val startPrev: Int = cycle.prevOf(startIndex)
    val startNode: Int = cycle[startIndex]

    val endNode: Int = cycle[endIndex]
    val endNext: Int = cycle.nextOf(endIndex)

    override val delta: Int = if (endNext == startNode) {
        Int.MAX_VALUE

    } else {
        dm[startPrev][endNode] +
                dm[startNode][endNext] -
                dm[startPrev][startNode] -
                dm[endNode][endNext]

    }

    override fun checkValidity(): Validity {
        val startIndex = cycle.indexOf(startNode)

        if (startIndex < 0) {
            return Validity.BROKEN
        }

        val startDir = startDir(startIndex, startPrev, cycle)

        if (startDir == Validity.BROKEN) {
            return Validity.BROKEN
        }

        val endIndex = cycle.indexOf(endNode)

        if (endIndex < 0) {
            return Validity.BROKEN
        }

        val endDir = endDir(endIndex, endNext, cycle)

        if (endDir == Validity.BROKEN) {
            return Validity.BROKEN
        }

        if (startDir != endDir) {
            return Validity.INVERTED
        }

        return Validity.VALID
    }

    private fun startDir(startIndex: Int, startPrev: Int, cycle: MutableList<Int>): Validity {
        if (cycle.prevOf(startIndex) == startPrev) {
            return Validity.VALID
        } else if (cycle.nextOf(startIndex) == startPrev) {
            return Validity.INVERTED
        }

        return Validity.BROKEN
    }

    private fun endDir(endIndex: Int, endNext: Int, cycle: MutableList<Int>): Validity {
        if (cycle.nextOf(endIndex) == endNext) {
            return Validity.VALID
        } else if (cycle.prevOf(endIndex) == endNext) {
            return Validity.INVERTED
        }

        return Validity.BROKEN
    }

}