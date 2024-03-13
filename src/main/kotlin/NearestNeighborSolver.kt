package org.example

import kotlin.random.Random

class NearestNeighborSolver(seed: Long = 42) : ISolver<TSProblem> {

    private val rng = Random(seed)

    override fun solve(instance: TSProblem): TSPSolution {
        val path = listOf(mutableListOf<Int>(), mutableListOf())
        val freeVertices = (0..<instance.dimension).toMutableList()

        val lastAdded = mutableListOf(
            freeVertices.removeAt(rng.nextInt(freeVertices.size)),
            -1,
        )
        path[0].add(lastAdded[0])

        // Start second cycle from the furthest free node
        lastAdded[1] = instance.distanceMatrix[lastAdded[0]].withIndex().maxBy { it.value }.index
        path[1].add(lastAdded[1])
        freeVertices.remove(lastAdded[1])

        var phase = 0
        do {
            var bestIndex = 0
            var bestDistance = Int.MAX_VALUE

            for (i in freeVertices.indices) {
                val d = instance.distanceMatrix[freeVertices[i]][lastAdded[phase]]
                if (d < bestDistance) {
                    bestIndex = i
                    bestDistance = d
                }
            }

            lastAdded[phase] = freeVertices.removeAt(bestIndex)
            path[phase].add(lastAdded[phase])

            phase = phase xor 1

        } while (freeVertices.size > 0)

        return TSPSolution(instance, path[0], path[1])
    }

}