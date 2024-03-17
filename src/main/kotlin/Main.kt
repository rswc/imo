package org.example

import org.example.core.Experiment
import org.example.tsp.NearestNeighborSolver
import org.example.tsp.RegretHeuristicSolver
import org.example.tsp.TSPReader
import org.example.tsp.TSPVisualizer

fun main() {
    val experiment = Experiment(
        listOf(NearestNeighborSolver(), NearestNeighborSolver(greedyCycle = true), RegretHeuristicSolver()),
        listOf(TSPReader.read("/kroA100.tsp"), TSPReader.read("/kroB100.tsp"))
    )

    experiment.run(100)
    experiment.saveLatex("tspResult.txt")
    experiment.saveBestSolutions(TSPVisualizer())
}