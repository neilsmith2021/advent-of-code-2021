import kotlin.math.abs

fun main() {

    fun part1(input: List<Int>): Int {
        val smallestPosition = input.minOrNull() ?: 0
        val largestPosition = input.maxOrNull() ?: 0

        tailrec fun processPositions(results: List<Int>, target: Int, max: Int, input: List<Int>): List<Int> {
            return if (target > max) results
            else {
                val fuelCost = input.sumOf {
                    abs(it - target)
                }
                processPositions(results + fuelCost, target + 1, max, input)
            }
        }

        val possibleCosts = processPositions(emptyList(), smallestPosition, largestPosition, input)
        return possibleCosts.minOrNull() ?: 0
    }

    fun part2(input: List<Int>): Int {
        val smallestPosition = input.minOrNull() ?: 0
        val largestPosition = input.maxOrNull() ?: 0

        tailrec fun calculateCost(acc: Int, cost: Int, distance: Int): Int =
            if (distance == 0) acc else calculateCost(acc + cost, cost + 1, distance - 1)

        tailrec fun processPositions(results: List<Int>, target: Int, max: Int, input: List<Int>): List<Int> {
            return if (target > max) results
            else {
                val fuelCost = input.sumOf {
                    calculateCost(0, 1, abs(it - target))
                }
                processPositions(results + fuelCost, target + 1, max, input)
            }
        }

        val possibleCosts = processPositions(emptyList(), smallestPosition, largestPosition, input)
        return possibleCosts.minOrNull() ?: 0
    }


    val sampleInput = readInput("day07_sample_1").first().split(",").map { it.toInt() }
    val input = readInput("day07_1").first().split(",").map { it.toInt() }

    check(part1(sampleInput) == 37)
    println("Part 1: ${part1(input)}")

    check(part2(sampleInput) == 168)
    println("Part 2: ${part2(input)}")
}
