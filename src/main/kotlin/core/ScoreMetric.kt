package org.example.core

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

    var minSolution: ISolution? = null
        private set

    var maxSolution: ISolution? = null
        private set

    fun update(solution: ISolution) {
        val score = solution.score()

        sum += score
        if (score < minScore) {
            minScore = score
            minSolution = solution
        }
        if (score > maxScore) {
            maxScore = score
            maxSolution = solution
        }
        count++
    }

}