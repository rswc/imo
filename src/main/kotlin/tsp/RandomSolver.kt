package org.example.tsp

import org.example.core.ISolver

class RandomSolver: ISolver<TSProblem> {
    override fun solve(instance: TSProblem, experimentStep: Int?): TSPSolution {
        val nodes = (0 until instance.dimension).toMutableList()
        nodes.shuffle()
        return TSPSolution(
            instance,
            nodes.slice(0 until (instance.dimension / 2)).toMutableList(),
            nodes.slice((instance.dimension / 2) until instance.dimension).toMutableList(),
        )
    }

    override fun getDisplayName(): String {
        return "Random Solver"
    }

}