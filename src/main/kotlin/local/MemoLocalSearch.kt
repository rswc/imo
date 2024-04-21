package org.example.local

import org.example.core.ISolver
import org.example.local.moves.EdgeMove
import org.example.local.moves.Move
import org.example.local.moves.VertexMove
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class MemoLocalSearch(private val presolver: ISolver<TSProblem>): ISolver<TSProblem> {

    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val dm = instance.distanceMatrix

        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA, initialSolution.cycleB)
        val firstCycleSize = cycles[0].size

        val LM = mutableListOf<Move>()
        val moveSet = mutableSetOf<Pair<Long, Long>>()

        // Cycle through new moves and add to LM if they improve the score
        for (start in 0 until instance.dimension) {
            val startCycle = (start >= firstCycleSize).compareTo(false)
            val startIndex = start - firstCycleSize * startCycle


            for (end in 0 until instance.dimension) {
                if (start == end) {
                    continue
                }

                val endCycle = (end >= firstCycleSize).compareTo(false)
                val endIndex = end - firstCycleSize * endCycle

                if (moveSet.contains(VertexMove.GetSignature(cycles[startCycle], cycles[endCycle], startIndex, endIndex))) {
                    continue
                }

                if (startCycle == endCycle) {
                    // Intracycle edge swap

                    val move = EdgeMove(dm, cycles[startCycle], startIndex, endIndex, instance.dimension)

                    if (move.delta < 0) {
                        LM.add(move)
                        moveSet.add(move.getSignature())
                    }

                } else {
                    // Intercycle vertex swap

                    val move = VertexMove(dm, cycles[startCycle], cycles[endCycle], startIndex, endIndex)

                    if (move.delta < 0) {
                        LM.add(move)
                        moveSet.add(move.getSignature())
                    }
                }
            }
        }

        do {
            LM.sortDescending()

            var executed = false
            for (i in LM.indices.reversed()) {
                val move = LM[i]

                // Check move validity
                when (move.checkValidity()) {
                    Move.Validity.BROKEN -> {
                        LM.removeAt(i)
                        moveSet.remove(move.getSignature())
                    }
                    Move.Validity.INVERTED -> {}
                    Move.Validity.VALID -> {
                        move.execute()
                        move.addNextMoves(dm, LM, moveSet)
                        LM.removeAt(i)
                        moveSet.remove(move.getSignature())
                        executed = true
                        break
                    }
                }
            }

        } while (executed)

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    override fun getDisplayName(): String {
        return "Memeo"
    }

}