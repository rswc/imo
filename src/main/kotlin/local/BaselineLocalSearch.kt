package org.example.local

import org.example.core.ISolver
import org.example.tsp.TSPSolution
import org.example.tsp.TSProblem

class BaselineLocalSearch(private val presolver: ISolver<TSProblem>, private val timeLimit: Long):
    ISolver<TSProblem> {
    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val targetTime = System.currentTimeMillis() + timeLimit
        val dm = instance.distanceMatrix
        val initialSolution = presolver.solve(instance) as TSPSolution
        val cycles = listOf(initialSolution.cycleA.toMutableList(), initialSolution.cycleB.toMutableList())

        val nodeToCycle = MutableList(instance.dimension) { 0 }
        for (node in cycles[1]) {
            nodeToCycle[node] = 1
        }

        var bestSolution = initialSolution
        var bestScore = initialSolution.score()
        var currentScore = bestScore

        val nodes = listOf(0, 1).flatMap { i -> cycles[i].indices.map { it to i } }.toMutableList()

        do {
            var delta: Int

            val iS = nodes.indices.random()
            val iE = nodes.indices.random()

            if (iS == iE) {
                continue
            }

            val start = nodes[iS]
            val end = nodes[iE]

            val cycleStart = start.second
            val cycleEnd = end.second

            if (cycleStart == cycleEnd && (0..1).random() == 1) {
                // intracycle edge swap

                val startPrev = cycles[cycleStart][(start.first - 1).mod(cycles[cycleStart].size)]
                val startNode = cycles[cycleStart][start.first]

                val endNode = cycles[cycleEnd][end.first]
                val endNext = cycles[cycleEnd][(end.first + 1).mod(cycles[cycleEnd].size)]

                if (endNext == startNode) {
                    continue
                }

                delta = dm[startPrev][endNode] +
                        dm[startNode][endNext] -
                        dm[startPrev][startNode] -
                        dm[endNode][endNext]

                var n = end.first - start.first + 1

                if (n < 0) {
                    n += instance.dimension
                }

                n /= 2

                for (i in 0 until n) {
                    val left = (start.first + i).mod(cycles[cycleEnd].size)
                    val right = (end.first - i).mod(cycles[cycleEnd].size)

                    cycles[cycleEnd][left] = cycles[cycleEnd][right].also { cycles[cycleEnd][right] = cycles[cycleEnd][left] }
                }

            } else {
                // vertex swap

                val startPrev = cycles[cycleStart][(start.first - 1).mod(cycles[cycleStart].size)]
                val startNode = cycles[cycleStart][start.first]
                val startNext = cycles[cycleStart][(start.first + 1).mod(cycles[cycleStart].size)]

                val endPrev = cycles[cycleEnd][(end.first - 1).mod(cycles[cycleEnd].size)]
                val endNode = cycles[cycleEnd][end.first]
                val endNext = cycles[cycleEnd][(end.first + 1).mod(cycles[cycleEnd].size)]

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

                cycles[cycleStart][start.first] = cycles[cycleEnd][end.first]
                    .also { cycles[cycleEnd][end.first] = cycles[cycleStart][start.first] }

                nodes[iS] = Pair(start.first, end.second)
                nodes[iE] = Pair(end.first, start.second)
            }

            currentScore += delta

            if (currentScore < bestScore) {
                bestScore = currentScore
                bestSolution = TSPSolution(instance, cycles[0].toMutableList(), cycles[1].toMutableList())
            }

        } while (System.currentTimeMillis() < targetTime)

        return bestSolution
    }

    override fun getDisplayName(): String {
        return "Baseline & ${presolver.getDisplayName()}"
    }

}