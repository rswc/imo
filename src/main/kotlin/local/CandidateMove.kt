package org.example.local

import kotlin.math.abs

class CandidateMove(
    dm: Array<IntArray>,
    cycles: List<MutableList<Int>>,
    private val nodeToCycle: MutableList<Int>,
    startIndex: Int,
    startCycle: Int,
    private val endIndex: Int,
    endCycle: Int,
): Move() {

    private val cycleStart = cycles[startCycle]
    private val cycleEnd = cycles[endCycle]

    override val delta: Int
    val trueStartIndex: Int

    init {
        if (startCycle == endCycle && abs(startIndex - endIndex) <= 1) {
            delta = Int.MAX_VALUE
            trueStartIndex = -1

        } else {
            val sC1 = cycleStart.wrapIndex(startIndex - 1)
            val sC2 = cycleStart.wrapIndex(startIndex + 1)

            val delta1 = deltaWithCenter(dm, sC1, endIndex)
            val delta2 = deltaWithCenter(dm, sC2, endIndex)

            if (delta1 < delta2) {
                delta = delta1
                trueStartIndex = sC1

            } else {
                delta = delta2
                trueStartIndex = sC2

            }

        }
    }

    private fun deltaWithCenter(dm: Array<IntArray>, startCenter: Int, endCenter: Int): Int {
        val sCP = cycleStart.prevOf(startCenter)
        val sCC = cycleStart[startCenter]
        val sCN = cycleStart.nextOf(startCenter)
        val eCP = cycleEnd.prevOf(endCenter)
        val eCC = cycleEnd[startCenter]
        val eCN = cycleEnd.nextOf(endCenter)

        return dm[sCP][eCC] +
                dm[eCC][sCN] -
                dm[sCP][sCC] -
                dm[sCC][sCN] +
                dm[eCP][sCC] +
                dm[sCC][eCN] -
                dm[eCP][eCC] -
                dm[eCC][eCN]
    }

    override fun checkValidity(): Validity {
        return Validity.VALID
    }

    override fun execute() {
        val trueStartNode = cycleStart[trueStartIndex]
        val endNode = cycleEnd[endIndex]

        cycleStart[trueStartIndex] = cycleEnd[endIndex]
            .also { cycleEnd[endIndex] = cycleStart[trueStartIndex] }
        nodeToCycle[trueStartNode] = nodeToCycle[endNode]
            .also { nodeToCycle[endNode] = nodeToCycle[trueStartNode] }
    }

    override fun getSignature(): Pair<Long, Long> {
        throw Exception("Signature of candidate move requested!")
    }

}