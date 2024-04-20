package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class MemoLocalSearch(private val presolver: ISolver<TSProblem>): ISolver<TSProblem> {

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

                    val move = EdgeMove(dm, startCycle, cycles[startCycle], startIndex, endIndex)

                    if (move.delta < 0) {
                        LM.add(move)
                    }

                } else {
                    // Intercycle vertex swap

                    val move = VertexMove(dm, startCycle, cycles[startCycle], cycles[endCycle], startCycle, endCycle)

                    if (move.delta < 0) {
                        LM.add(move)
                    }
                }
            }
        }

        // TODO: sort LM in reverse order, so deletions are faster?
        LM.sort()
        for (i in LM.indices) {
            val move = LM[i]

            // Check move validity
            when (move.checkValidity()) {
                Move.Validity.BROKEN -> LM.removeAt(i)
                Move.Validity.INVERTED -> {}
                Move.Validity.VALID -> TODO("Execute")
            }
        }

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    override fun getDisplayName(): String {
        return "Memeo"
    }

}