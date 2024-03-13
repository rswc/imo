package org.example

data class TSPSolution(
    val instance: TSProblem,
    val cycleA: MutableList<Int>,
    val cycleB: MutableList<Int>
) : ISolution
