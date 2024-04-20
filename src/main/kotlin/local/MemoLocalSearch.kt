package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem
import java.util.PriorityQueue

class MemoLocalSearch(private val presolver: ISolver<TSProblem>): ISolver<TSProblem> {

    data class Move(
        val startNode: Int,
        val endNode: Int,
        val delta: Int
    )

    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val dm = instance.distanceMatrix

        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA, initialSolution.cycleB)
        val nodeToCycle = MutableList(instance.dimension) { 0 }
        cycles[1].forEach { nodeToCycle[it] = 1 }

        val LM = PriorityQueue<Move>()
        var delta: Int

        // Cycle through new moves and add to LM if they improve the score
        for (start in 0 until instance.dimension) {
            for (end in 0 until instance.dimension) {
                if (start == end) {
                    continue
                }

                val startCycle = nodeToCycle[start]
                val endCycle = nodeToCycle[end]

                if (startCycle == endCycle) {
                    // Intracycle edge swap

                    val startPrev = cycles[startCycle][(start - 1).mod(cycles[startCycle].size)]
                    val startNode = cycles[startCycle][start]

                    val endNode = cycles[endCycle][end]
                    val endNext = cycles[endCycle][(end + 1).mod(cycles[endCycle].size)]

                    if (endNext == startNode) {
                        continue
                    }

                    delta = dm[startPrev][endNode] +
                            dm[startNode][endNext] -
                            dm[startPrev][startNode] -
                            dm[endNode][endNext]

                } else {
                    // Intercycle vertex swap

                    val startPrev = cycles[startCycle][(start - 1).mod(cycles[startCycle].size)]
                    val startNode = cycles[startCycle][start]
                    val startNext = cycles[startCycle][(start + 1).mod(cycles[startCycle].size)]

                    val endPrev = cycles[endCycle][(end - 1).mod(cycles[endCycle].size)]
                    val endNode = cycles[endCycle][end]
                    val endNext = cycles[endCycle][(end + 1).mod(cycles[endCycle].size)]

                    delta = dm[startNode][endPrev] +
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

                }

                if (delta < 0) {
                    LM.add(Move(start, end, delta))
                }
            }
        }

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    override fun getDisplayName(): String {
        return "Memeo"
    }

}