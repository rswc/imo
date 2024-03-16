package org.example

import org.example.core.Experiment
import org.example.tsp.NearestNeighborSolver
import org.example.tsp.TSPReader

fun main() {
    val experiment = Experiment(
        listOf(NearestNeighborSolver(), NearestNeighborSolver(greedyCycle = true)),
        listOf(TSPReader.read("/kroA100.tsp"), TSPReader.read("/kroB100.tsp"))
    )

    experiment.run(10)
    experiment.saveLatex("tspResult.txt")
}