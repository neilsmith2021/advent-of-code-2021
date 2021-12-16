fun main() {

    data class Edge(
        val fromVertex: Pair<Int, Int>,
        val toVertex: Pair<Int, Int>,
        val weight: Int
    )

    class Graph(input: List<String>) {
        val width: Int
        val height: Int
        val vertices: Array<IntArray>
        val edges = mutableListOf<Edge>()
        val adjacencyList = mutableMapOf<Pair<Int, Int>, MutableList<Edge>>()

        init {
            vertices = input.map { line ->
                line.toCharArray().map { digit -> digit.digitToInt() }.toIntArray()
            }.toTypedArray()

            width = input[0].length
            height = input.size

            fun addEdge(fromX: Int, fromY: Int, toX: Int, toY: Int) {
                val edge = Edge(Pair(fromX, fromY), Pair(toX, toY), vertices[toY][toX])
                edges.add(edge)
                adjacencyList.getOrPut(Pair(fromX, fromY)) { mutableListOf() }.add(edge)
            }

            vertices.indices.forEach { y ->
                vertices[y].indices.forEach { x ->
                    if (x > 0) addEdge(x, y, x - 1, y)
                    if (x < width - 1) addEdge(x, y, x + 1, y)
                    if (y > 0) addEdge(x, y, x, y - 1)
                    if (y < height - 1) addEdge(x, y, x, y + 1)
                }
            }
        }
    }

    fun shortestPath(graph: Graph, start: Pair<Int, Int>, finish: Pair<Int, Int>): Int {
        val distanceTo = Array(graph.height) { IntArray(graph.width) { Int.MAX_VALUE } }
        val edgeTo: Array<Array<Edge?>> = Array(graph.height) { Array(graph.width) { null } }
        val priorityQueue: MutableList<Pair<Pair<Int, Int>, Int>> = mutableListOf()

        fun relax(edge: Edge) {
            val from = edge.fromVertex
            val to = edge.toVertex
            if (distanceTo[to.second][to.first] > distanceTo[from.second][from.first] + edge.weight) {
                distanceTo[to.second][to.first] = distanceTo[from.second][from.first] + edge.weight
                edgeTo[to.second][to.first] = edge

                priorityQueue.firstOrNull { it.first == to }?.let {
                    priorityQueue.remove(it)
                    priorityQueue.add(Pair(it.first, it.second - distanceTo[to.second][to.first]))
                } ?: priorityQueue.add(Pair(to, distanceTo[to.second][to.first]))
            }
        }

        distanceTo[start.second][start.first] = 0
        priorityQueue.add(Pair(start, distanceTo[start.second][start.first]))

        while (priorityQueue.isNotEmpty()) {
            val vertex = priorityQueue.minByOrNull { it.second }!!
            priorityQueue.remove(vertex)
            graph.adjacencyList[vertex.first]?.forEach { edge -> relax(edge) }
        }

        return distanceTo[finish.second][finish.first]
    }

    fun part1(input: List<String>): Int {
        val graph = Graph(input)
        return shortestPath(graph, Pair(0, 0), Pair(input[0].length - 1, input.size - 1))
    }

    fun part2(input: List<String>): Int {
        val width = input[0].length
        val height = input.size

        val expandedInput = List(height * 5) { y ->
            CharArray(width * 5) { x ->
                val newRiskLevel =  ((input[y % height][x % width].digitToInt()) + (x / width) + (y / height))
                (if (newRiskLevel >= 10) (newRiskLevel + 1) % 10 else newRiskLevel).digitToChar()
            }.joinToString("")
        }

        return part1(expandedInput)
    }

    val sampleInput = readInput("day15_sample_1")
    val input = readInput("day15_1")

    check(part1(sampleInput) == 40)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput) == 315)
    println("Part2: ${part2(input)}")
}