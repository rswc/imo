package org.example.tsp

import org.example.core.IInstance
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class TSProblem(val points: List<List<Int>>, override val name: String) : IInstance {

    val distanceMatrix: Array<IntArray>
    val dimension: Int = points.size

    init {
        distanceMatrix = Array(dimension) {i ->
            IntArray(dimension) {j ->
                points[i]
                    .zip(points[j])
                    .map { (it.first - it.second).toDouble().pow(2.0) }
                    .reduce {acc, d -> acc + d}
                    .let { sqrt(it) }
                    .roundToInt()
            }
        }
    }

}