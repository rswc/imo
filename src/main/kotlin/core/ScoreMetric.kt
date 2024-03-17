package org.example.core

import kotlin.math.max
import kotlin.math.min

class ScoreMetric {

    private var sum = 0.0
    var minScore = Double.MAX_VALUE
        private set
    var maxScore = Double.MIN_VALUE
        private set
    var count = 0
        private set

    val mean: Double
        get() {
            return sum / count
        }

    fun update(solution: ISolution) {
        val score = solution.score()

        sum += score
        minScore = min(minScore, score)
        maxScore = max(maxScore, score)
        count++
    }

}