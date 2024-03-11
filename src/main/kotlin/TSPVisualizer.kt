package org.example

import kotlin.math.max
import kotlin.math.min

class TSPVisualizer {

    private var minCoords: List<Double> = listOf()
    private var coordsSpan: List<Double> = listOf()
    private var scale = 10.0

    fun generateDot(problem: TSProblem, solution: TSPSolution, scale: Double = 10.0): String {
        val result = StringBuilder("digraph G {")
        var i = 0
        
        minCoords = problem.points
            .reduce { acc, point -> listOf(min(acc[0], point[0]), min(acc[1], point[1])) }
            .map { it.toDouble() }
        coordsSpan = problem.points
            .reduce { acc, point -> listOf(max(acc[0], point[0]), max(acc[1], point[1])) }
            .mapIndexed { index, span -> (span - minCoords[index]) }
        this.scale = scale

        for (coords in problem.points) {
            assert(coords.size == 2)

            result.append("""|n${i} [
                |   label = ${i++}
                |   pos = "${getPos(coords)}!"
                |]
                |
            |""".trimMargin())
        }

        for (pair in solution.cycle.zipWithNext()) {
            result.append("n${pair.first} -> n${pair.second};\n")
        }

        result.append("n${solution.cycle.last()} -> n${solution.cycle.first()};\n}")

        return result.toString()
    }

    private fun getPos(coordinates: List<Int>): String {
        return coordinates
            .mapIndexed { index, coord -> (coord - minCoords[index]) / coordsSpan[index] * scale }
            .joinToString(",")
    }

}