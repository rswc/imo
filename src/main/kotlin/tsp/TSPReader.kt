package org.example.tsp

object TSPReader {

    fun read(name: String): TSProblem {
        var isCoordSection = false
        val coords: MutableList<List<Int>> = mutableListOf()
        var friendlyName = "TSP instance"

        this::class.java.getResourceAsStream(name)?.bufferedReader()?.use { reader ->
            reader.forEachLine {line ->
                if (line.startsWith("NODE_COORD_SECTION")) {
                    isCoordSection = true
                } else if (isCoordSection && !line.startsWith("EOF")) {
                    val terms = line.split(" ")
                    coords.add(terms.subList(1, terms.size).map { it.toInt() })
                } else if (line.startsWith("NAME:")) {
                    friendlyName = line.split(" ")[1]
                }
            }
        }

        return TSProblem(coords, friendlyName)
    }

}