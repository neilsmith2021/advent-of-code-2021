import kotlin.math.min

fun main() {

    fun parseInput(input: List<String>): List<IntRange> {
        val line = input[0].split("=", "..", ",")
        return listOf(line[1].toInt()..line[2].toInt(), line[4].toInt()..line[5].toInt())
    }

    fun calculatePaths(xRange: IntRange, yRange: IntRange): List<Pair<Int, Int>> {
        val maxX = xRange.last
        val minY = min(yRange.first, yRange.last)

        tailrec fun moveProbe(xPosition: Int, yPosition: Int, xIncrement: Int, yIncrement: Int): Boolean {
            return if (xPosition > xRange.last || yPosition < minY) false
            else if (xPosition in xRange && yPosition in yRange) true
            else {
                moveProbe(
                    xPosition + xIncrement,
                    yPosition + yIncrement,
                    if (xIncrement <= 1) 0 else xIncrement - 1,
                    yIncrement - 1
                )
            }
        }

        val candidates = mutableListOf<Pair<Int, Int>>()
        (0..maxX).forEach { xIncrement ->
            (-maxX..maxX).forEach { yIncrement ->
                if (moveProbe(0, 0, xIncrement, yIncrement)) candidates.add(Pair(xIncrement, yIncrement))
            }
        }
        return candidates
    }

    fun part1(xRange: IntRange, yRange: IntRange): Int {

        tailrec fun maxHeight(x: Int, y: Int, acc: Int): Int {
            return if (y == 0) acc else maxHeight(if (x <= 1) 0 else x - 1, y - 1, acc + y)
        }

        val candidates = calculatePaths(xRange, yRange)
        val bestInitialVelocity = candidates.first { coord -> coord.second == candidates.maxOf { it.second } }
        return maxHeight(bestInitialVelocity.first, bestInitialVelocity.second, 0)
    }

    fun part2(xRange: IntRange, yRange: IntRange): Int {
        return calculatePaths(xRange, yRange).size
    }

    val sampleInput = parseInput(readInput("day17_sample_1"))
    val input = parseInput(readInput("day17_1"))

    check(part1(sampleInput[0], sampleInput[1]) == 45)
    println("Part1: ${part1(input[0], input[1])}")

    check(part2(sampleInput[0], sampleInput[1]) == 112)
    println("Part2: ${part2(input[0], input[1])}")
}