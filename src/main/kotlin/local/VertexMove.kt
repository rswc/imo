package org.example.local

class VertexMove (
    dm: Array<IntArray>,
    val startCycleIndex: Int,
    private val cycleStart: MutableList<Int>,
    private val cycleEnd: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
) : Move() {

    val startPrev: Int = cycleStart.prevOf(startIndex)
    val startNode: Int = cycleStart[startIndex]
    val startNext: Int = cycleStart.nextOf(startIndex)

    val endPrev: Int = cycleEnd.prevOf(endIndex)
    val endNode: Int = cycleEnd[endIndex]
    val endNext: Int = cycleEnd.nextOf(endIndex)

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
        val startIndex = cycleStart.indexOf(startNode)

        if (startIndex < 0) {
            return Validity.BROKEN
        }

        val endIndex = cycleEnd.indexOf(endNode)

        if (endIndex < 0) {
            return Validity.BROKEN
        }

        return Validity.VALID
    }

}