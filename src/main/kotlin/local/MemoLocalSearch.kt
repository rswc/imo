package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class MemoLocalSearch(private val presolver: ISolver<TSProblem>): ISolver<TSProblem> {

    data class Move(
        val isEdge: Boolean,
        val startCycle: Int,
        val startPrev: Int,
        val startNode: Int,
        val startNext: Int,
        val endPrev: Int,
        val endNode: Int,
        val endNext: Int,
        val delta: Int
    ): Comparable<Move> {
        override fun compareTo(other: Move): Int {
            return delta - other.delta
        }
    }

    enum class Validity {
        VALID, INVERTED, BROKEN
    }

    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val dm = instance.distanceMatrix

        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA, initialSolution.cycleB)
        val firstCycleSize = cycles[0].size

        val LM = mutableListOf<Move>()

        // Cycle through new moves and add to LM if they improve the score
        for (start in 0 until instance.dimension) {
            for (end in 0 until instance.dimension) {
                if (start == end) {
                    continue
                }

                val startCycle = (start >= firstCycleSize).compareTo(false)
                val endCycle = (end >= firstCycleSize).compareTo(false)
                val startIndex = start - firstCycleSize * startCycle
                val endIndex = end - firstCycleSize * endCycle

                if (startCycle == endCycle) {
                    // Intracycle edge swap

                    val startPrev = cycles[startCycle].prevOf(startIndex)
                    val startNode = cycles[startCycle][startIndex]

                    val endNode = cycles[endCycle][endIndex]
                    val endNext = cycles[endCycle].nextOf(endIndex)

                    if (endNext == startNode) {
                        continue
                    }

                    val delta = dm[startPrev][endNode] +
                            dm[startNode][endNext] -
                            dm[startPrev][startNode] -
                            dm[endNode][endNext]

                    if (delta < 0) {
                        LM.add(
                            Move(
                                true,
                                startCycle,
                                startPrev,
                                startNode,
                                -1,
                                -1,
                                endNode,
                                endNext,
                                delta
                            )
                        )
                    }

                } else {
                    // Intercycle vertex swap

                    val startPrev = cycles[startCycle].prevOf(startIndex)
                    val startNode = cycles[startCycle][startIndex]
                    val startNext = cycles[startCycle].nextOf(startIndex)

                    val endPrev = cycles[endCycle].prevOf(endIndex)
                    val endNode = cycles[endCycle][endIndex]
                    val endNext = cycles[endCycle].nextOf(endIndex)

                    var delta = dm[startNode][endPrev] +
                            dm[startNode][endNext] +
                            dm[endNode][startPrev] +
                            dm[endNode][startNext] -
                            dm[startNode][startPrev] -
                            dm[startNode][startNext] -
                            dm[endNode][endPrev] -
                            dm[endNode][endNext]

                    // When the two nodes are next to each other, they share an edge
                    if (startNode == endNext) {
                        delta += dm[startNode][startPrev] + dm[startNode][endNode]
                    }
                    if (endNode == startNext) {
                        delta += dm[endNode][endPrev] + dm[endNode][startNode]
                    }

                    if (delta < 0) {
                        LM.add(
                            Move(
                                false,
                                startCycle,
                                startPrev,
                                startNode,
                                startNext,
                                endPrev,
                                endNode,
                                endNext,
                                delta
                            )
                        )
                    }
                }
            }
        }

        // TODO: sort LM in reverse order, so deletions are faster?
        LM.sort()
        for (i in LM.indices) {
            val move = LM[i]

            // Check move validity
            when (checkValidity(move, cycles)) {
                Validity.BROKEN -> LM.removeAt(i)
                Validity.INVERTED -> TODO("Pass")
                Validity.VALID -> TODO("Execute")
            }
        }

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    private fun checkValidity(move: Move, cycles: List<MutableList<Int>>): Validity {
        val cycleStart = cycles[move.startCycle]

        if (move.isEdge) {
            val startIndex = cycleStart.indexOf(move.startNode)

            if (startIndex < 0) {
                return Validity.BROKEN
            }

            val startDir = edgeStartDir(startIndex, move.startPrev, cycleStart)

            if (startDir == Validity.BROKEN) {
                return Validity.BROKEN
            }

            val endIndex = cycleStart.indexOf(move.endNode)

            if (endIndex < 0) {
                return Validity.BROKEN
            }

            val endDir = edgeEndDir(endIndex, move.endNext, cycleStart)

            if (endDir == Validity.BROKEN) {
                return Validity.BROKEN
            }

            if (startDir != endDir) {
                return Validity.INVERTED
            }
        }

        return Validity.VALID
    }

    private fun edgeStartDir(startIndex: Int, startPrev: Int, cycle: MutableList<Int>): Validity {
        if (cycle.prevOf(startIndex) == startPrev) {
            return Validity.VALID
        } else if (cycle.nextOf(startIndex) == startPrev) {
            return Validity.INVERTED
        }

        return Validity.BROKEN
    }

    private fun edgeEndDir(endIndex: Int, endNext: Int, cycle: MutableList<Int>): Validity {
        if (cycle.nextOf(endIndex) == endNext) {
            return Validity.VALID
        } else if (cycle.prevOf(endIndex) == endNext) {
            return Validity.INVERTED
        }

        return Validity.BROKEN
    }

    override fun getDisplayName(): String {
        return "Memeo"
    }

}