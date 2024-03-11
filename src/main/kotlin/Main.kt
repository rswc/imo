package org.example

import java.io.File

fun main() {
    val problem = TSPReader.read("/kroA100.tsp")
    val sol = NearestNeighborSolver().solve(problem)

    File("solution.dot").writeText(TSPVisualizer().generateDot(problem, sol))
}