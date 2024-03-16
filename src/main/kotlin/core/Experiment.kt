package org.example.core

import java.io.File

class Experiment<PROBLEM : IInstance>(
    val solvers: List<ISolver<PROBLEM>>,
    val instances: List<PROBLEM>
) {

    val results = solvers.associateWith { mutableMapOf<PROBLEM, ScoreMetric>().withDefault { ScoreMetric() } }

    fun run(steps: Int) {
        solvers.forEach { solver ->
            instances.forEach { instance ->
                (0..steps).forEach { step ->
                    val solution = solver.solve(instance)

                    results[solver]!!.getValue(instance).update(solution)
                }
            }
        }
    }

}