package org.example

import java.io.File

fun main() {
    val problem = TSPReader.read("/kroA20.tsp")
    val sol = NearestNeighborSolver().solve(problem)

    println("Score: ${sol.score()}")

    File("solution.dot").writeText(TSPVisualizer().generateDot(problem, sol))
}