fun main() {
    fun part1(input: List<Pair<Int, Int>>): Int {

        val distanceForwards = input.sumOf { it.first }
        val depth = input.sumOf { it.second }

        return distanceForwards * depth
    }

    fun part2(input: List<Pair<Int, Int>>): Int {

        val distanceForwards = input.sumOf { it.first }

        tailrec fun calculateDepth(aim: Int, depth: Int, head: Pair<Int, Int>, tail: List<Pair<Int, Int>>): Int {
            return if (tail.isEmpty()) {
                depth + aim * head.first
            } else {
                val newAim = aim + head.second
                calculateDepth(
                    newAim,
                    depth + newAim * head.first,
                    tail.first(),
                    tail.drop(1)
                )
            }
        }

        val depth = calculateDepth(0, 0, input.first(), input.drop(1))

        return depth * distanceForwards
    }

    fun mapInput(input: List<String>): List<Pair<Int, Int>> {
        return input.map {
            val command = it.split(" ")
            when (command[0]) {
                "forward" -> Pair(command[1].toInt(), 0)
                "down" -> Pair(0, command[1].toInt())
                else -> Pair(0, -command[1].toInt())
            }
        }
    }

//    val input = readInput("day02_sample_1")
    val input = readInput("day02_1")
    val mappedInput = mapInput(input)
    println(part1(mappedInput))
    println(part2(mappedInput))
}
