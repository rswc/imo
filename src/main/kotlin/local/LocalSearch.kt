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

        do {
            var delta: Int
            var swapped = false

            startNodes.shuffle()
            endNodes.shuffle()

            for (start in startNodes) {
                for (end in endNodes) {
                    if (start.second != end.second) {
                        // intercycle

                        val startPrev = cycles[start.second][(start.first - 1).mod(cycles[start.second].size)]
                        val startNode = cycles[start.second][start.first]
                        val startNext = cycles[start.second][(start.first + 1).mod(cycles[start.second].size)]

                        val endPrev = cycles[end.second][(end.first - 1).mod(cycles[end.second].size)]
                        val endNode = cycles[end.second][end.first]
                        val endNext = cycles[end.second][(end.first + 1).mod(cycles[end.second].size)]

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

                        if (delta < 0) {
                            cycles[start.second][start.first] = cycles[end.second][end.first]
                                .also { cycles[end.second][end.first] = cycles[start.second][start.first] }
                            swapped = true
                        }

                    } else {
                        // intracycle

                        val startPrev = cycles[start.second][(start.first - 1).mod(cycles[start.second].size)]
                        val startNode = cycles[start.second][start.first]
                        val startNext = cycles[start.second][(start.first + 1).mod(cycles[start.second].size)]

                        val endPrev = cycles[end.second][(end.first - 1).mod(cycles[end.second].size)]
                        val endNode = cycles[end.second][end.first]
                        val endNext = cycles[end.second][(end.first + 1).mod(cycles[end.second].size)]

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

                        if (delta < 0) {
                            cycles[start.second][start.first] = cycles[end.second][end.first]
                                .also { cycles[end.second][end.first] = cycles[start.second][start.first] }
                            swapped = true
                        }
                    }
                }
            }
        } while (swapped)

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    override fun getDisplayName(): String {
        return "Local Search"
    }

}