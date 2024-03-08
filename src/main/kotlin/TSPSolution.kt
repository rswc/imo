package org.example

data class TSPSolution(
    val instance: TSProblem,
    val cycle: MutableList<Int>
) : ISolution