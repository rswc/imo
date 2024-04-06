package org.example

import org.example.core.Experiment
import org.example.local.BaselineLocalSearch
import org.example.local.GreedyLocalSearch
import org.example.local.SteepLocalSearch
import org.example.tsp.*

fun main() {
    val bestGreedySolver = RegretHeuristicSolver()

    val experiment = Experiment(
        listOf(
//            GreedyLocalSearch(RandomSolver(), swapEdges = false),
//            GreedyLocalSearch(RandomSolver(), swapEdges = true),
//            GreedyLocalSearch(bestGreedySolver, swapEdges = false),
//            GreedyLocalSearch(bestGreedySolver, swapEdges = true),
//            SteepLocalSearch(RandomSolver(), swapEdges = false),
//            SteepLocalSearch(RandomSolver(), swapEdges = true),
//            SteepLocalSearch(bestGreedySolver, swapEdges = false),
//            SteepLocalSearch(bestGreedySolver, swapEdges = true),
            BaselineLocalSearch(RandomSolver(), 32L),
            BaselineLocalSearch(bestGreedySolver, 32L),
        ),
        listOf(TSPReader.read("/kroA100.tsp"), TSPReader.read("/kroB100.tsp"))
    )

    experiment.run(100)
    experiment.saveLatex("tspResult.txt")
    experiment.saveBestSolutions(TSPVisualizer())
}