package org.example.tsp

import org.example.core.ISolver
import kotlin.random.Random

class RegretHeuristicSolver(private val weight: Double = 0.4, seed: Long = 42) : ISolver<TSProblem> {

    private val rng = Random(seed)

    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val freeVertices = MutableList(instance.dimension) { 1 }
        val startVerA = rng.nextInt(0, freeVertices.size)
        val startVerB = instance.distanceMatrix.indices.maxByOrNull { instance.distanceMatrix[it][startVerA] }!!

        freeVertices[startVerA] = 0
        freeVertices[startVerB] = 0

        var cycleA = initCycle(startVerA, instance.distanceMatrix, freeVertices)
        var cycleB = initCycle(startVerB, instance.distanceMatrix, freeVertices)

        freeVertices[cycleA[1]] = 0
        freeVertices[cycleB[1]] = 0

        while (freeVertices.sum() > 0) {
            cycleA = extendCycle(cycleA, instance.distanceMatrix, freeVertices)

            if (freeVertices.sum() > 0) {
                cycleB = extendCycle(cycleB, instance.distanceMatrix, freeVertices)
            }
        }

        cycleA.removeAt(cycleA.size - 1)
        cycleB.removeAt(cycleB.size - 1)

        return TSPSolution(instance, cycleA.toMutableList(), cycleB.toMutableList())
    }

    private fun initCycle(index: Int, matrix: Array<IntArray>, freeVertices: MutableList<Int>): MutableList<Int> {
        val row = matrix[index]
        var minLength = Int.MAX_VALUE
        var bestVertex: Int? = null

        row.forEachIndexed { vertex, length ->
            if (freeVertices[vertex] == 1 && length < minLength) {
                minLength = length
                bestVertex = vertex
            }
        }

        bestVertex?.let {
            freeVertices[it] = 0
            return mutableListOf(index, it, index)
        } ?: throw IllegalStateException("No suitable initial vertex found.")
    }

    private fun distanceDiff(matrix: Array<IntArray>, cycle: List<Int>, index: Int, checkedVertex: Int): Int {
        val prev = cycle[index - 1]
        val next = cycle[index]
        return matrix[prev][checkedVertex] + matrix[checkedVertex][next] - matrix[prev][next]
    }

    private fun findBestWithRegret(cycle: List<Int>, matrix: Array<IntArray>, freeVertices: MutableList<Int>): Int {
        val vertices = freeVertices.indices.filter { freeVertices[it] == 1 }
        val distances = mutableListOf<List<Int>>()

        vertices.forEach { freeVertex ->
            distances.add((1 until cycle.size).map { cycleVertex ->
                distanceDiff(matrix, cycle, cycleVertex, freeVertex)
            })
        }

        val regret = distances.mapIndexed { index, dist ->
            val sortedDist = dist.sorted()
            val twoRegret = sortedDist[1] - sortedDist[0]
            Pair(vertices[index], twoRegret - weight * sortedDist[0])
        }

        return regret.maxByOrNull { it.second }?.first ?: throw IllegalStateException("No vertex found.")
    }

    private fun extendCycle(cycle: MutableList<Int>, matrix: Array<IntArray>, freeVertices: MutableList<Int>): MutableList<Int> {
        val bestVertex = findBestWithRegret(cycle, matrix, freeVertices)
        val bestInsertion = (1 until cycle.size).minByOrNull { distanceDiff(matrix, cycle, it, bestVertex) }!!

        freeVertices[bestVertex] = 0
        cycle.add(bestInsertion, bestVertex)

        return cycle
    }

    override fun getDisplayName(): String {
        return "Regret Heuristic"
    }
}
