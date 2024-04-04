package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class LocalSearch(private val presolver: ISolver<TSProblem>): ISolver<TSProblem> {
    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val dm = instance.distanceMatrix
        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA, initialSolution.cycleB)

        val startNodes = listOf(0, 1).flatMap { i -> cycles[i].indices.map { it to i } }.toMutableList()
        val endNodes = startNodes.toMutableList()

        startNodes.shuffle()
        endNodes.shuffle()

        for (start in startNodes) {
            for (end in endNodes) {
                if (start.second != end.second) {
                    // intercycle

                    val delta = dm[end.first][(start.first - 1).mod(cycles[start.second].size)] +
                            dm[end.first][(start.first + 1).mod(cycles[start.second].size)] -
                            dm[start.first][(start.first - 1).mod(cycles[start.second].size)] -
                            dm[start.first][(start.first + 1).mod(cycles[start.second].size)] +
                            dm[start.first][(end.first - 1).mod(cycles[end.second].size)] +
                            dm[start.first][(end.first + 1).mod(cycles[end.second].size)] -
                            dm[end.first][(end.first - 1).mod(cycles[end.second].size)] -
                            dm[end.first][(end.first + 1).mod(cycles[end.second].size)]

                    if (delta > 0) {
                        // TODO: commit vertex swap and restart loop
                    }

                } else {
                    // intracycle
                    // TODO: do vertex or edge swap, depending on configuration
                }
            }
        }

        return TSPSolution(instance, mutableListOf(), mutableListOf())
    }

    override fun getDisplayName(): String {
        return "Local Search"
    }

}