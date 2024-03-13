package org.example

import kotlin.random.Random

class NearestNeighborSolver(seed: Long = 42) : ISolver<TSProblem> {

    private val rng = Random(seed)

    override fun solve(instance: TSProblem): TSPSolution {
        val path = listOf(mutableListOf<Int>(), mutableListOf())
        val freeVertices = (0..<instance.dimension).toMutableList()

        path[0].add(freeVertices.removeAt(rng.nextInt(freeVertices.size)))

        // Start second cycle from the furthest free node
        val furthestNode = instance.distanceMatrix[path[0].first()].withIndex().maxBy { it.value }.index
        path[1].add(furthestNode)
        freeVertices.remove(furthestNode)

        var phase = 0
        do {
            var bestIndex = Pair(0, 0)
            var bestDistanceDelta = Int.MAX_VALUE

            for (j in path[phase].indices) {
                val currentToNext = instance.distanceMatrix[path[phase][j]]
                    .elementAtOrNull(path[phase].elementAtOrElse(j + 1) { Int.MAX_VALUE }) ?: 0

                for (i in freeVertices.indices) {
                    val candidateToNext = instance.distanceMatrix[freeVertices[i]]
                        .elementAtOrNull(path[phase].elementAtOrElse(j + 1) { Int.MAX_VALUE }) ?: 0
                    val currentToCandidate = instance.distanceMatrix[freeVertices[i]][path[phase][j]]

                    val delta = currentToCandidate + candidateToNext - currentToNext
                    if (delta < bestDistanceDelta) {
                        bestIndex = Pair(i, j + 1)
                        bestDistanceDelta = delta
                    }
                }
            }

            path[phase].add(bestIndex.second, freeVertices.removeAt(bestIndex.first))

            phase = phase xor 1

        } while (freeVertices.size > 0)

        return TSPSolution(instance, path[0], path[1])
    }

}