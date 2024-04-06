package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class SteepLocalSearch(private val presolver: ISolver<TSProblem>, private val swapEdges: Boolean = false): ISolver<TSProblem> {
    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val dm = instance.distanceMatrix
        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA, initialSolution.cycleB)

        val startNodes = listOf(0, 1).flatMap { i -> cycles[i].indices.map { it to i } }.toMutableList()
        val endNodes = startNodes.toMutableList()

        do {
            var bestDelta = 0
            var bestStart = startNodes[0]
            var bestEnd = startNodes[0]
            var bestIS = 0
            var bestIE = 0

            for (iS in startNodes.indices) {
                for (iE in endNodes.indices) {
                    val start = startNodes[iS]
                    val end = endNodes[iE]

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

                        val delta = dm[startPrev][endNode] +
                                dm[startNode][endNext] -
                                dm[startPrev][startNode] -
                                dm[endNode][endNext]

                        if (delta < bestDelta) {
                            bestDelta = delta
                            bestStart = start
                            bestEnd = end
                            bestIS = iS
                            bestIE = iE
                        }

                    } else {
                        // vertex swap

                        val startPrev = cycles[start.second][(start.first - 1).mod(cycles[start.second].size)]
                        val startNode = cycles[start.second][start.first]
                        val startNext = cycles[start.second][(start.first + 1).mod(cycles[start.second].size)]

                        val endPrev = cycles[end.second][(end.first - 1).mod(cycles[end.second].size)]
                        val endNode = cycles[end.second][end.first]
                        val endNext = cycles[end.second][(end.first + 1).mod(cycles[end.second].size)]

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

                        if (delta < bestDelta) {
                            bestDelta = delta
                            bestStart = start
                            bestEnd = end
                            bestIS = iS
                            bestIE = iE
                        }
                    }
                }
            }

            if (bestDelta < 0) {
                if (swapEdges && bestStart.second == bestEnd.second) {
                    var n = bestEnd.first - bestStart.first + 1

                    if (n < 0) {
                        n += instance.dimension
                    }

                    n /= 2

                    val c = bestStart.second

                    for (i in 0 until n) {
                        val left = (bestStart.first + i).mod(cycles[c].size)
                        val right = (bestEnd.first - i).mod(cycles[c].size)

                        cycles[c][left] = cycles[c][right].also { cycles[c][right] = cycles[c][left] }
                    }

                } else {
                    cycles[bestStart.second][bestStart.first] = cycles[bestEnd.second][bestEnd.first]
                        .also { cycles[bestEnd.second][bestEnd.first] = cycles[bestStart.second][bestStart.first] }

                    startNodes[bestIS] = Pair(bestStart.first, bestEnd.second)
                    endNodes[bestIE] = Pair(bestEnd.first, bestStart.second)
                }
            }

        } while (bestDelta < 0)

        return TSPSolution(instance, cycles[0], cycles[1])
    }

    override fun getDisplayName(): String {
        return "Steep & ${presolver.getDisplayName()} & ${if (swapEdges) "Edge" else "Vertex"}"
    }

}