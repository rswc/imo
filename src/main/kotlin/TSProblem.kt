package org.example

import kotlin.math.roundToInt

class TSProblem(points: List<List<Int>>) {

    var distanceMatrix: Array<IntArray>

    init {
        val dimension = points.size
        distanceMatrix = Array(dimension) {i ->
            IntArray(dimension) {j ->
                points[i]
                    .zip(points[j])
                    .map { Math.pow((it.first - it.second).toDouble(), 2.0) }
                    .reduce {acc, d -> acc + d}
                    .let { Math.sqrt(it) }
                    .roundToInt()
            }
        }
    }

}