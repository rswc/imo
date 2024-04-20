package org.example.local

class EdgeMove(
    dm: Array<IntArray>,
    private val dimension: Int,
    private val cycle: MutableList<Int>,
    val startPrev: Int,
    val startNode: Int,
    val endNode: Int,
    val endNext: Int,
) : Move() {

    private var startIndex: Int = -1
    private var endIndex: Int = -1
    var dir: Int = 1

    override val delta: Int = if (endNext == startNode) {
        Int.MAX_VALUE

    } else {
        dm[startPrev][endNode] +
        dm[startNode][endNext] -
        dm[startPrev][startNode] -
        dm[endNode][endNext]

    }

    constructor(
        dm: Array<IntArray>,
        cycle: MutableList<Int>,
        startIndex: Int,
        endIndex: Int,
        dimension: Int
    ) : this(
        dm,
        dimension,
        cycle,
        cycle.prevOf(startIndex),
        cycle[startIndex],
        cycle[endIndex],
        cycle.nextOf(endIndex),
    )

    override fun checkValidity(): Validity {
        startIndex = cycle.indexOf(startNode)

        if (startIndex < 0) {
            return Validity.BROKEN
        }

        val startDir = startDir(startIndex, startPrev, cycle)

        if (startDir == Validity.BROKEN) {
            return Validity.BROKEN
        }

        endIndex = cycle.indexOf(endNode)

        if (endIndex < 0 || endIndex == startIndex) {
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

    override fun execute() {
        // WARNING: this assumes the move has been validated this round!
        var n = endIndex - startIndex + 1

        if (n < 0) {
            n += dimension
        }

        n /= 2

        for (i in 0 until n) {
            val left = (startIndex + i + dir).mod(cycle.size)
            val right = (endIndex - i - dir).mod(cycle.size)

            cycle[left] = cycle[right].also { cycle[right] = cycle[left] }
        }
    }

    override fun getSignature(): Pair<Long, Long> {
        return Pair((startNode.toLong() shl 32) + startPrev.toLong(), (endNode.toLong() shl 32) + endNext.toLong())
    }

    fun inverted(dm: Array<IntArray>, si: Int, ei: Int): EdgeMove {
        //TODO: how ?????
        return EdgeMove(
            dm,
            dimension,
            cycle,
            cycle.nextOf(si),
            cycle[si],
            cycle[ei],
            cycle.prevOf(ei),
        )
    }

    private fun startDir(startIndex: Int, startPrev: Int, cycle: MutableList<Int>): Validity {
        if (cycle.prevOf(startIndex) == startPrev) {
            dir = 0
            return Validity.VALID
        } else if (cycle.nextOf(startIndex) == startPrev) {
            dir = 1
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


    override fun toString(): String {
        return "EdgeMove(start = $startNode, end = $endNode, delta = $delta)"
    }

}