package org.example

import kotlin.math.pow
import kotlin.random.Random

class NearestNeighborSolver(seed: Long = 42) : ISolver<TSProblem> {

    private val rng = Random(seed)
    private lateinit var inst: TSProblem

    private fun getDistance(a: Int, b: Int): Double {
        return inst.points[a]
            .zip(inst.points[b])
            .fold(0.0) { acc, pair -> acc + (pair.first - pair.second).toDouble().pow(2.0) }
    }

    override fun solve(instance: TSProblem): TSPSolution {
        inst = instance
        val path = mutableListOf<Int>()
        val freeVertices = (0..<instance.dimension).toMutableList()

        var lastAdded = freeVertices.removeAt(rng.nextInt(freeVertices.size))
        path.add(lastAdded)

        do {
            var bestIndex = 0
            var bestDistance = Double.MAX_VALUE

            for (i in freeVertices.indices) {
                val d = getDistance(freeVertices[i], lastAdded)
                if (d < bestDistance) {
                    bestIndex = i
                    bestDistance = d
                }
            }

            lastAdded = freeVertices.removeAt(bestIndex)
            path.add(lastAdded)

        } while (freeVertices.size > 0)

        return TSPSolution(instance, path)
    }

}