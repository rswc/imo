package org.example

import kotlin.random.Random

class NearestNeighborSolver(seed: Long = 42) : ISolver<TSProblem> {

    private val rng = Random(seed)

    override fun solve(instance: TSProblem): TSPSolution {
        val path = mutableListOf<Int>()
        val freeVertices = (0..<instance.dimension).toMutableList()

        path.add(freeVertices.removeAt(rng.nextInt(freeVertices.size)))

        do {
            //TODO: find nearest neighbor
            path.add(freeVertices.removeAt(rng.nextInt(freeVertices.size)))
        } while (freeVertices.size > 0)

        return TSPSolution(instance, path)
    }

}