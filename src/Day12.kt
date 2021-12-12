fun main() {

    fun mapInput(input: List<String>): Map<String, List<String>> {
        val mappedInput = input.map { it.split("-") }
        val nodes = mappedInput.flatten().distinct()
        val twoWayConnections = mappedInput.flatMap {
            listOf(it[0] to it[1], it[1] to it[0])
        }

        return nodes.associateWith { node ->
            val candidates = twoWayConnections.filter { it.first == node }
            val endPoints = candidates.map { it.second }
            endPoints
        }
    }

    fun part1(input: List<String>): Int {
        val mappedInput = mapInput(input)

        fun follow(node: String, path: List<String>): List<List<String>> {
            return mappedInput[node]!!.filter { nextNode ->
                nextNode.isUpperCase() || !path.contains(nextNode)
            }.flatMap {
                if (it == "end") {
                    listOf(path + it)
                } else {
                    follow(it, path + it)
                }
            }
        }

        return follow("start", listOf("start")).size
    }

    fun anyDuplicateSmallCavesInPath(path: List<String>): Boolean {
        return path.filter { !it.isUpperCase() }
            .groupingBy { it }
            .eachCount()
            .filter { it.value > 1 }
            .isNotEmpty()
    }

    fun part2(input: List<String>): Int {
        val mappedInput = mapInput(input)

        fun follow(node: String, path: List<String>): List<List<String>> {
            return mappedInput[node]!!.filter { nextNode ->
                nextNode != "start" &&
                        (nextNode.isUpperCase() || !path.contains(nextNode) || !anyDuplicateSmallCavesInPath(path))
            }.flatMap {
                if (it == "end") {
                    listOf(path + it)
                } else {
                    follow(it, path + it)
                }
            }
        }

        return follow("start", listOf("start")).size
    }

    val sampleInput1 = readInput("day12_sample_1")
    val sampleInput2 = readInput("day12_sample_2")
    val sampleInput3 = readInput("day12_sample_3")
    val input = readInput("day12_1")

    check(part1(sampleInput1) == 10)
    check(part1(sampleInput2) == 19)
    check(part1(sampleInput3) == 226)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput1) == 36)
    check(part2(sampleInput2) == 103)
    check(part2(sampleInput3) == 3509)
    println("Part2: ${part2(input)}")
}