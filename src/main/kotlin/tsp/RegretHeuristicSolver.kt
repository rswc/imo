package org.example

import kotlin.random.Random

class RegretHeuristicSolver(seed: Long = 42) : ISolver<TSProblem> {

    private val rng = Random(seed)


    override fun solve(instance: TSProblem): TSPSolution {

        //for (row in instance.distanceMatrix) {
           // for (element in row) {
            //    print("$element    ")
            //}
           // println()
        //}


        val freeVertices = MutableList(instance.dimension) { 1 }
        val startVerA = rng.nextInt(0, freeVertices.size)
        val startVerB = instance.distanceMatrix.indices.maxByOrNull { instance.distanceMatrix[it][startVerA] }!!

        freeVertices[startVerA] = 0
        freeVertices[startVerB] = 0

        var cycleA = initCycle(startVerA, instance.distanceMatrix, freeVertices.toMutableList())
        var cycleB = initCycle(startVerB, instance.distanceMatrix, freeVertices.toMutableList())
        //println(cycleA.toList())
        //println(cycleB.toList())

        freeVertices[cycleA[1]] = 0
        freeVertices[cycleB[1]] = 0
        //println(cycleA.toList())
        //println(freeVertices)
        while (freeVertices.sum() > 0) {

            cycleA = extendCycle(cycleA, instance.distanceMatrix, freeVertices)
            //println(cycleA.toList())
            //println(freeVertices)
            if (freeVertices.sum() > 0){
                cycleB = extendCycle(cycleB, instance.distanceMatrix, freeVertices)
                //println(cycleB.toList())
            //println(freeVertices)
            }
        }
        cycleA.removeAt(cycleA.size - 1)
        cycleB.removeAt(cycleB.size - 1)


        println(cycleA.toList())
        println(cycleB.toList())
        println(freeVertices)
        return TSPSolution(instance, cycleA.toList(), cycleB.toList())
    }

    private fun initCycle(index: Int, matrix: Array<IntArray>, freeVertices: MutableList<Int>): MutableList<Int> {
        val row = matrix.map { it[index] }
        var minLength = Int.MAX_VALUE
        var bestVertex: Int? = null

        row.forEachIndexed { vertex, length ->
            if (freeVertices[vertex] == 1 && length < minLength) {
                minLength = length
                bestVertex = vertex
            }
        }
        //println(minLength)
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
        //println("X")
        //println(vertices)
        //println(distances)
        val regret = distances.mapIndexed { index, dist ->
            val sortedDist = dist.sorted()
            val twoRegret = sortedDist[1] - sortedDist[0]
            Pair(vertices[index], twoRegret)
        }
        //println(regret)

        val allZeroRegret = regret.all { it.second == 0 }

        return if (allZeroRegret) {
            // Zwróć wierzchołek o minimalnej odległości
            val minDistanceVertex = vertices.minByOrNull { vertex ->
                distances[vertices.indexOf(vertex)].minOrNull() ?: Int.MAX_VALUE
            }
            minDistanceVertex ?: throw IllegalStateException("No vertex found.")
        } else {
            // Zwróć wierzchołek o największym regret
            regret.maxByOrNull { it.second }?.first ?: throw IllegalStateException("No vertex found.")
        }
    }

    private fun extendCycle(cycle: MutableList<Int>, matrix: Array<IntArray>, freeVertices: MutableList<Int>): MutableList<Int> {
        val bestVertex = findBestWithRegret(cycle, matrix, freeVertices)
        val bestInsertion = (1 until cycle.size).minByOrNull { distanceDiff(matrix, cycle, it, bestVertex) }!!

        freeVertices[bestVertex] = 0
        cycle.add(bestInsertion, bestVertex)

        return cycle
    }
}
