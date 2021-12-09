fun main() {

    fun mapInput(input: List<String>): Array<IntArray> {
        return Array<IntArray>(input.size) {
            input[it].toCharArray().map(Character::getNumericValue).toIntArray()
        }
    }

    fun getNeighbours(input: Array<IntArray>, x: Int, y: Int): List<Int> {
        val left = if (x > 0) input[y][x - 1] else 9
        val top = if (y > 0)input[y - 1][x] else 9
        val right = if (x < input[0].size - 1) input[y][x + 1] else 9
        val bottom = if (y < input.size - 1) input[y + 1][x] else 9
        return listOf(left, top, right, bottom)
    }

    fun part1(input: Array<IntArray>): List<Pair<Int, Int>> {

        val lowPoints = mutableListOf<Pair<Int, Int>>()

        for (x in input[0].indices) {
            for (y in input.indices) {
                val neighbours = getNeighbours(input, x, y)
                if (neighbours.count { it <= input[y][x] } == 0) {
                    lowPoints.add(Pair(x, y))
                }
            }
        }

        return lowPoints
    }

    fun getRiskLevel(input: Array<IntArray>, lowPoints: List<Pair<Int, Int>>): Int {
        return lowPoints.map { (x, y) ->
            input[y][x] + 1
        }.sum()
    }

    fun checkNeighbours(
        grid: Array<IntArray>,
        seenPoints: MutableSet<Pair<Int, Int>>,
        coord: Pair<Int, Int>
    ): Int {
        return if (seenPoints.contains(coord)) 0
        else {
            seenPoints.add(coord)
            val neighbours = getNeighbours(grid, coord.first, coord.second)
            val left =
                if (neighbours[0] < 9) checkNeighbours(grid, seenPoints, Pair(coord.first - 1, coord.second)) else 0
            val top =
                if (neighbours[1] < 9) checkNeighbours(grid, seenPoints, Pair(coord.first, coord.second - 1)) else 0
            val right =
                if (neighbours[2] < 9) checkNeighbours(grid, seenPoints, Pair(coord.first + 1, coord.second)) else 0
            val bottom =
                if (neighbours[3] < 9) checkNeighbours(grid, seenPoints, Pair(coord.first, coord.second + 1)) else 0
            1 + left + top + right + bottom
        }
    }

    fun part2(grid: Array<IntArray>, lowPoints: List<Pair<Int, Int>>): Int {
        val basinSizes = lowPoints.map { coord ->
            val seenPoints = mutableSetOf<Pair<Int, Int>>()
            val basinSize = checkNeighbours(grid, seenPoints, coord)
            basinSize
        }
        return basinSizes.sortedDescending().take(3).reduce(Int::times)
    }

    val sampleInput = readInput("day09_sample_1")
    val input = readInput("day09_1")

    val sampleGrid = mapInput(sampleInput)
    val sampleLowPoints = part1(sampleGrid)
    check(getRiskLevel(sampleGrid, sampleLowPoints) == 15)

    val grid = mapInput(input)
    val lowPoints = part1(grid)
    println("Part1: ${getRiskLevel(grid, lowPoints)}")

    check(part2(sampleGrid, sampleLowPoints) == 1134)
    println("Part2: ${part2(grid, lowPoints)}")
}