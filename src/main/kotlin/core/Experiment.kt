package org.example.core

import org.example.tsp.TSPSolution
import org.example.tsp.TSPVisualizer
import java.io.File
import kotlin.system.measureTimeMillis

class Experiment<PROBLEM : IInstance>(
    val solvers: List<ISolver<PROBLEM>>,
    val instances: List<PROBLEM>
) {

    val results = solvers.associateWith { instances.associateWithTo(mutableMapOf()) { ScoreMetric() } }

    fun run(steps: Int) {
        solvers.forEach { solver ->
            instances.forEach { instance ->
                    var minTime = 1000.00
                    var maxTime = 0.00
                    var sumTime = 0.00
                    (0 until steps).forEach { step ->
                        val timeElapsed = measureTimeMillis {
                        val solution = solver.solve(instance, experimentStep = step)
                        results[solver]!!.getValue(instance).update(solution)
                    }
                        sumTime += timeElapsed
                        if (timeElapsed < minTime) {
                            minTime = timeElapsed.toDouble()
                        }
                        if (timeElapsed > maxTime) {
                            maxTime = timeElapsed.toDouble()
                        }

                        }
                val avgTime = sumTime / steps.toDouble()
                println("Solver ${solver.getDisplayName()} on instance ${instance.name} took: $avgTime ($minTime - $maxTime) ms")
                    }


            }
        }



    fun saveLatex(path: String, precision: Int = 2) {
        val table = StringBuilder("\\begin{center}\n\\begin{tabular}{|c|")
        instances.forEach { table.append("c|") }
        table.append("}\n\\hline\n ")
        instances.forEach { table.append("& ${it.name} ") }
        table.append("\\\\\n")

        fun Double.format(scale: Int) = "%.${scale}f".format(this)

        solvers.forEach { solver ->
            table.append("\\hline\n${solver.getDisplayName()} ")

            instances.forEach { instance ->
                val metrics = results[solver]!!.getValue(instance)
                table.append("& ${metrics.mean.format(precision)} (${metrics.minScore.format(precision)}--${metrics.maxScore.format(precision)}) ")
            }

            table.append("\\\\\n")
        }

        table.append("\\hline\n\\end{tabular}\n\\end{center}")

        File(path).writeText(table.toString())
    }

    fun saveBestSolutions(visualizer: TSPVisualizer) {
        solvers.forEach { solver ->
            instances.forEach { instance ->
                val metrics = results[solver]!!.getValue(instance)

                File("sol_${solver.getDisplayName()}_${instance.name}.dot")
                    .writeText(visualizer.generateDot(metrics.minSolution as TSPSolution))
            }
        }
    }

}