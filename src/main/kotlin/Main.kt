package org.example

import java.io.File

fun main() {
    val problem = TSPReader.read("/kroB100.tsp")
    val sol = NearestNeighborSolver(greedyCycle = true).solve(problem)

    println("Score: ${sol.score()}")

    File("solution.dot").writeText(TSPVisualizer().generateDot(problem, sol))
}