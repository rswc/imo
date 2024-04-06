package org.example.tsp

import kotlin.math.max
import kotlin.math.min

class TSPVisualizer {

    private var minCoords: List<Double> = listOf()
    private var coordsSpan: List<Double> = listOf()
    private var scale = 10.0

    fun generateDot(solution: TSPSolution, scale: Double = 10.0): String {
        val problem = solution.instance
        val result = StringBuilder("digraph G {\n")
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
                |   color = "${if (solution.cycleA[0] == i || solution.cycleB[0] == i) "blue" else "black"}"
                |   pos = "${getPos(coords)}!"
                |   style = "filled"
                |   fillcolor = "${if (i in solution.cycleA) "black" else "red"}"
                |   label = ${i}
                |]
                |
            |""".trimMargin())

            i++
        }

        printCycle(result, solution.cycleA)
        printCycle(result, solution.cycleB, color = "red")

        result.append("}")

        return result.toString()
    }

    private fun printCycle(result: StringBuilder, cycle: List<Int>, color: String = "black") {
        for (pair in cycle.zipWithNext()) {
            result.append("n${pair.first} -> n${pair.second} [color=\"$color\"];\n")

        }

        result.append("n${cycle.last()} -> n${cycle.first()} [color=\"$color\"];\n")
    }

    private fun getPos(coordinates: List<Int>): String {
        return coordinates
            .mapIndexed { index, coord -> (coord - minCoords[index]) / coordsSpan[index] * scale }
            .joinToString(",")
    }

}