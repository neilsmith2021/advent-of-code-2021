fun main() {

    fun part1(input: List<Int>, iterations: Int): Int {

        tailrec fun processNewDay(dayCount: Int, head: List<Int>): List<Int> {
            return if (dayCount == 0) head
            else {
                var newFishCount = 0
                val fishList = head.map {
                    if (it == 0) {
                        newFishCount++
                        6
                    } else {
                        it - 1
                    }
                }.toMutableList()
                repeat(newFishCount) { fishList.add(8) }
                processNewDay(dayCount - 1, fishList)
            }
        }

        return processNewDay(iterations, input).size
    }

    fun part2(input: List<Int>, iterations: Int): Long {
        val listSummary = input.groupingBy { it }.eachCount().mapValues { it.value.toLong() }
        tailrec fun processNewDay(dayCount: Int, frequencyMap: Map<Int, Long>): Map<Int, Long> {
            return if (dayCount == 0) frequencyMap
            else {
                val newMap = mutableMapOf<Int, Long>()
                (0..8).forEach { key ->
                    if (key == 0) {
                        newMap[6] = frequencyMap[0] ?: 0
                        newMap[8] = frequencyMap[0] ?: 0
                    } else {
                        newMap[key - 1] = (newMap[key - 1] ?: 0) + (frequencyMap[key] ?: 0)
                    }
                }
                processNewDay(dayCount - 1, newMap)
            }
        }
        return processNewDay(iterations, listSummary).values.sum()
    }

    val sampleInput = readInput("day06_sample_1").first().split(",").map { it.toInt() }
    val input = readInput("day06_1").first().split(",").map { it.toInt() }

    check(part1(sampleInput, 18) == 26)
    check(part1(sampleInput, 80) == 5934)
    println("Part 1: ${part1(input, 80)}")

    check(part2(sampleInput, 256) == 26984457539)
    println("Part 2: ${part2(input, 256)}")
}