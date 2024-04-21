package org.example.local.moves

import org.example.local.nextOf
import org.example.local.prevOf
import org.example.local.wrapIndex

class VertexMove (
    dm: Array<IntArray>,
    private val cycleStart: MutableList<Int>,
    private val cycleEnd: MutableList<Int>,
    startIndex: Int,
    endIndex: Int,
    private val dimension: Int,
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

    override fun addNextMoves(
        dm: Array<IntArray>,
        LM: MutableList<Move>,
        moveSet: MutableSet<Pair<Long, Long>>,
        cycles: List<MutableList<Int>>
    ) {
        genVertexMoves(startIndex, cycleStart, cycleEnd, dm, LM, moveSet)
        genVertexMoves(endIndex, cycleEnd, cycleStart, dm, LM, moveSet)

        genEdgeMoves(startIndex, dm, LM, moveSet, cycleStart, cycleEnd)
        genEdgeMoves(endIndex, dm, LM, moveSet, cycleEnd, cycleStart)
    }

    private fun genEdgeMoves(
        iA: Int,
        dm: Array<IntArray>,
        LM: MutableList<Move>,
        moveSet: MutableSet<Pair<Long, Long>>,
        cycle: MutableList<Int>,
        otherCycle: MutableList<Int>
    ) {
        for (si in -2..2) {
            val sIndex = cycle.wrapIndex(iA + si)

            for (eIndex in cycle.indices) {
                if (sIndex == eIndex) {
                    continue
                }

                // Intracycle edge swap
                val move = EdgeMove(dm, cycle, otherCycle, sIndex, eIndex, dimension)

                if (move.delta < 0 && !moveSet.contains(VertexMove.GetSignature(cycle, cycle, sIndex, eIndex))) {
                    LM.add(move)
                    moveSet.add(move.getSignature())
                }

                val iMove = move.inverted(dm, sIndex, eIndex)

                if (iMove.delta < 0 && !moveSet.contains(VertexMove.GetSignature(cycle, cycle, sIndex, eIndex))) {
                    LM.add(iMove)
                    moveSet.add(iMove.getSignature())
                }

                val move2 = EdgeMove(dm, cycle, otherCycle, eIndex, sIndex, dimension)

                if (move2.delta < 0 && !moveSet.contains(VertexMove.GetSignature(cycle, cycle, eIndex, sIndex))) {
                    LM.add(move)
                    moveSet.add(move.getSignature())
                }

                val iMove2 = move.inverted(dm, sIndex, eIndex)

                if (iMove2.delta < 0 && !moveSet.contains(VertexMove.GetSignature(cycle, cycle, sIndex, eIndex))) {
                    LM.add(iMove2)
                    moveSet.add(iMove2.getSignature())
                }
            }
        }
    }

    private fun genVertexMoves(
        iA: Int,
        cycle: MutableList<Int>,
        otherCycle: MutableList<Int>,
        dm: Array<IntArray>,
        LM: MutableList<Move>,
        moveSet: MutableSet<Pair<Long, Long>>,
    ) {
        for (si in -1..1) {
            val sIdx = cycle.wrapIndex(iA + si)

            for (eIdx in otherCycle.indices) {
                if (sIdx == eIdx) {
                    continue
                }

                // Intercycle vertex swap
                val move = VertexMove(dm, cycle, otherCycle, sIdx, eIdx, dimension)

                if (move.delta < 0 && !moveSet.contains(GetSignature(cycle, otherCycle, sIdx, eIdx))) {
                    LM.add(move)
                    moveSet.add(move.getSignature())
                }
            }
        }
    }

    companion object {
        fun GetSignature(cycleStart: MutableList<Int>, cycleEnd: MutableList<Int>, startIndex: Int, endIndex: Int): Pair<Long, Long> {
            val sP = cycleStart.prevOf(startIndex).toLong()
            val sN = cycleStart[startIndex].toLong()
            val eN = cycleEnd[endIndex].toLong()
            val eX =  cycleEnd.nextOf(endIndex).toLong()
            return Pair((sN shl 32) + sP, (eN shl 32) + eX)
        }
    }

    override fun toString(): String {
        return "VertexMove(start = $startNode, end = $endNode, delta = $delta)"
    }

}