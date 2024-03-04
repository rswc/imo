package org.example

object TSPReader {

    fun read(name: String): TSProblem {
        var isCoordSection = false
        val coords: MutableList<List<Int>> = mutableListOf()

        this::class.java.getResourceAsStream(name)?.bufferedReader()?.use { reader ->
            reader.forEachLine {line ->
                if (line.startsWith("NODE_COORD_SECTION")) {
                    isCoordSection = true
                } else if (isCoordSection && !line.startsWith("EOF")) {
                    val terms = line.split(" ")
                    coords.add(terms.subList(1, terms.size).map { it.toInt() })
                }
            }
        }

        return TSProblem(coords)
    }

}