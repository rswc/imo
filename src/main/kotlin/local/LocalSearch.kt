package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class LocalSearch(private val presolver: ISolver<TSProblem>, private val swapEdges: Boolean = false): ISolver<TSProblem> {
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
                    if (swapEdges && start.second == end.second) {
                        // intracycle edge swap

                        if (start.first == end.first) {
                            continue
                        }

                        val startPrev = cycles[start.second][(start.first - 1).mod(cycles[start.second].size)]
                        val startNode = cycles[start.second][start.first]

                        val endNode = cycles[end.second][end.first]
                        val endNext = cycles[end.second][(end.first + 1).mod(cycles[end.second].size)]

                        if (endNext == startNode) {
                            continue
                        }

                        delta = dm[startPrev][endNode] +
                                dm[startNode][endNext] -
                                dm[startPrev][startNode] -
                                dm[endNode][endNext]

                        if (delta < 0) {
                            var n = end.first - start.first + 1

                            if (n < 0) {
                                n += instance.dimension
                            }

                            n /= 2

                            val c = start.second

                            for (i in 0 until n) {
                                val left = (start.first + i).mod(cycles[c].size)
                                val right = (end.first - i).mod(cycles[c].size)

                                cycles[c][left] = cycles[c][right].also { cycles[c][right] = cycles[c][left] }
                            }

                            swapped = true
                        }

                    } else {
                        // vertex swap

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