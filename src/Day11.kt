fun main() {

    fun bumpAllOctopusEnergyLevels(grid: Array<IntArray>) {
        for (y in grid.indices)
            for (x in grid[y].indices)
                grid[y][x]++
    }

    fun flashNeighbours(grid: Array<IntArray>, y: Int, x: Int) {
        val neighbours = listOf(
            Pair(x - 1, y - 1),
            Pair(x, y - 1),
            Pair(x + 1, y - 1),
            Pair(x - 1, y + 1),
            Pair(x, y + 1),
            Pair(x + 1, y + 1),
            Pair(x - 1, y),
            Pair(x + 1, y),
        )

        neighbours.forEach { (x, y) ->
            if (x >= 0 && y >= 0 && y < grid.size && x < grid[y].size) {
                grid[y][x]++
            }
        }
    }

    fun performFlashes(grid: Array<IntArray>) {
        val alreadyFlashed = mutableSetOf<Pair<Int, Int>>()

        fun flashOctopusesNotAlreadyFlashed() {
            for (y in grid.indices)
                for (x in grid[y].indices) {
                    if (grid[y][x] > 9 && !alreadyFlashed.contains(Pair(x, y))) {
                        alreadyFlashed.add(Pair(x, y))
                        flashNeighbours(grid, y, x)
                    }
                }
        }

        while (true) {
            val currentFlashSize = alreadyFlashed.size
            flashOctopusesNotAlreadyFlashed()
            if (currentFlashSize == alreadyFlashed.size) break
        }

    }

    fun resetEnergyLevelsPostFlash(grid: Array<IntArray>): Int {
        var flashCount = 0
        for (y in grid.indices)
            for (x in grid[y].indices)
                if (grid[y][x] > 9) {
                    flashCount++
                    grid[y][x] = 0
                }
        return flashCount
    }

    fun part1(input: List<String>, iterations: Int): Int {
        val grid = Array(input.size) { line -> IntArray(input[line].length) { char -> input[line][char].digitToInt() } }
        var totalFlashes = 0

        repeat(iterations) {
            bumpAllOctopusEnergyLevels(grid)
            performFlashes(grid)
            totalFlashes += resetEnergyLevelsPostFlash(grid)
        }

        return totalFlashes
    }

    fun part2(input: List<String>): Int {
        val grid = Array(input.size) { line -> IntArray(input[line].length) { char -> input[line][char].digitToInt() } }
        var iterations = 0

        while (true) {
            iterations++
            bumpAllOctopusEnergyLevels(grid)
            performFlashes(grid)
            if (resetEnergyLevelsPostFlash(grid) == grid[0].size * grid.size) break
        }

        return iterations
    }

    val sampleInput1 = readInput("day11_sample_1")
    val sampleInput2 = readInput("day11_sample_2")
    val input = readInput("day11_1")

    check(part1(sampleInput1, 2) == 9)
    check(part1(sampleInput2, 10) == 204)
    check(part1(sampleInput2, 100) == 1656)
    println("Part1: ${part1(input, 100)}")

    check(part2(sampleInput2) == 195)
    println("Part2: ${part2(input)}")
}