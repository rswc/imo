package org.example

import org.example.core.Experiment
import org.example.local.*
import org.example.tsp.*

fun main() {
    val experiment = Experiment(
        listOf(
            MemoLocalSearch(RandomSolver()),
//            CandidateSearch(RandomSolver()),
//            RegretHeuristicSolver(),
            SteepLocalSearch(RandomSolver(), swapEdges = true),
        ),
        listOf(TSPReader.read("/kroA200.tsp"))
    )

    experiment.run(100)
    experiment.saveLatex("tspResult.txt")
    experiment.saveBestSolutions(TSPVisualizer())
}