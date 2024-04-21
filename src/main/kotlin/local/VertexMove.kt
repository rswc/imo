package org.example.local

class VertexMove (
    dm: Array<IntArray>,
    private val cycleStart: MutableList<Int>,
    private val cycleEnd: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
) : Move() {

    val startPrev: Int = cycleStart.prevOf(startIndex)
    val startNode: Int = cycleStart[startIndex]
    val startNext: Int = cycleStart.nextOf(startIndex)
    private var startIndex: Int = -1

    val endPrev: Int = cycleEnd.prevOf(endIndex)
    val endNode: Int = cycleEnd[endIndex]
    val endNext: Int = cycleEnd.nextOf(endIndex)
    private var endIndex: Int = -1

    override val delta: Int

    init {
        var d = dm[startNode][endPrev] +
                dm[startNode][endNext] +
                dm[endNode][startPrev] +
                dm[endNode][startNext] -
                dm[startNode][startPrev] -
                dm[startNode][startNext] -
                dm[endNode][endPrev] -
                dm[endNode][endNext]

        // When the two nodes are next to each other, they share an edge
        if (startNode == endNext) {
            d += dm[startNode][startPrev] + dm[startNode][endNode]
        }
        if (endNode == startNext) {
            d += dm[endNode][endPrev] + dm[endNode][startNode]
        }

        delta = d
    }

    override fun checkValidity(): Validity {
        startIndex = cycleStart.indexOf(startNode)

        if (startIndex < 0) {
            return Validity.BROKEN
        }

        if (!(
                (cycleStart.prevOf(startIndex) == startPrev && cycleStart.nextOf(startIndex) == startNext)
                || (cycleStart.nextOf(startIndex) == startPrev && cycleStart.prevOf(startIndex) == startNext)
            )) {
            return Validity.BROKEN
        }

        endIndex = cycleEnd.indexOf(endNode)

        if (endIndex < 0) {
            return Validity.BROKEN
        }

        if (!(
                (cycleEnd.prevOf(endIndex) == endPrev && cycleEnd.nextOf(endIndex) == endNext)
                || (cycleEnd.nextOf(endIndex) == endPrev && cycleEnd.prevOf(endIndex) == endNext)
            )) {
            return Validity.BROKEN
        }

        return Validity.VALID
    }

    override fun execute() {
        // WARNING: this assumes the move has been validated this round!
        cycleStart[startIndex] = cycleEnd[endIndex]
            .also { cycleEnd[endIndex] = cycleStart[startIndex] }
    }

    override fun getSignature(): Pair<Long, Long> {
        return Pair((startNode.toLong() shl 32) + startPrev.toLong(), (endNode.toLong() shl 32) + endNext.toLong())
    }

    override fun toString(): String {
        return "VertexMove(start = $startNode, end = $endNode, delta = $delta)"
    }

}