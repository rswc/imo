package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class CandidateSearch(private val presolver: ISolver<TSProblem>, private val neighborCount: Int = 10): ISolver<TSProblem> {

    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val dm = instance.distanceMatrix
        val nb = dm.map { row ->
            row.mapIndexed { idx, dist -> Pair(idx, dist) }
                .sortedBy { it.second }
                .subList(1, neighborCount + 1)
                .map { it.first }
                .toIntArray()
        }.toTypedArray()

        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA, initialSolution.cycleB)
        val firstCycleSize = cycles[0].size

        val nodeToCycle = MutableList(instance.dimension) { 0 }
        cycles[1].forEach { nodeToCycle[it] = 1 }

        do {
            var bestMove: Move = DummyMove()

            for (start in 0 until instance.dimension) {
                val startCycle = (start >= firstCycleSize).compareTo(false)
                val startIndex = start - firstCycleSize * startCycle

                for (neighborIndex in 0 until neighborCount) {
                    val endNode = nb[startIndex][neighborIndex]
                    val endCycle = nodeToCycle[endNode]
                    val endIndex = cycles[endCycle].indexOf(endNode)

                    val move = CandidateMove(
                        dm,
                        cycles,
                        nodeToCycle,
                        startIndex,
                        startCycle,
                        endIndex,
                        endCycle
                    )

                    if (move < bestMove) {
                        bestMove = move
                    }
                }
            }

            if (bestMove.delta < 0) {
                bestMove.execute()
            }

        } while (bestMove.delta < 0)

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    override fun getDisplayName(): String {
        return "Candidate"
    }

}