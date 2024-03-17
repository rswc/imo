package org.example.tsp

import org.example.core.ISolution

data class TSPSolution(
    val instance: TSProblem,
    val cycleA: MutableList<Int>,
    val cycleB: MutableList<Int>
) : ISolution {

    private fun cycleLength(cycle: List<Int>): Double {
        return cycle
            .zipWithNext()
            .fold(0.0) {acc, pair -> acc + instance.distanceMatrix[pair.first][pair.second]} +
                instance.distanceMatrix[cycleA.first()][cycleA.last()]
    }

    override fun score(): Double {
        return cycleLength(cycleA) + cycleLength(cycleB)
    }

}
