fun main() {

    fun mapPixels(input: List<String>): Array<CharArray> {
        return Array(input.size + 4) { line ->
            if (line < 2 || line > input.size + 1) CharArray(input[0].length + 4) { '0' }
            else ("..${input[line - 2]}..").toCharArray().map { if (it == '#') '1' else '0' }.toCharArray()
        }
    }

    fun enhanceImage(input: List<String>, iterations: Int): Int {
        val algorithm = input[0].map { if (it == '#') '1' else '0' }.toCharArray()

        fun wrapGrid(grid: Array<CharArray>): Array<CharArray> {
            val gridRows = grid.size
            val gridColumns = grid[0].size
            val outsideCharacter = grid[0][0]

            return Array(gridColumns + 2) { row ->
                if (row == 0 || row == gridRows + 1) CharArray(gridColumns + 2) { outsideCharacter }
                else {
                    (outsideCharacter + grid[row - 1].joinToString("") + outsideCharacter).toCharArray()
                }
            }
        }

        fun applyAlgorithm(grid: Array<CharArray>): Array<CharArray> {
            val gridRows = grid.size
            val gridColumns = grid[0].size

            return wrapGrid(Array(gridRows) { y ->
                if (y == 0 || y == grid.lastIndex) CharArray(gridColumns) { algorithm[if (grid[0][0] == '0') 0 else 511] }
                else CharArray(gridColumns) { x ->
                    if (x == 0 || x == grid[0].lastIndex) algorithm[if (grid[0][0] == '0') 0 else 511]
                    else {
                        val indexString = listOf(
                            grid[y - 1][x - 1],
                            grid[y - 1][x],
                            grid[y - 1][x + 1],
                            grid[y][x - 1],
                            grid[y][x],
                            grid[y][x + 1],
                            grid[y + 1][x - 1],
                            grid[y + 1][x],
                            grid[y + 1][x + 1]
                        ).joinToString("")
                        val index = indexString.toInt(2)
                        algorithm[index]
                    }
                }
            })
        }

        tailrec fun iterate(acc: Array<CharArray>, countDown: Int): Array<CharArray> {
            return if (countDown == 0) acc
            else iterate(applyAlgorithm(acc), countDown - 1)
        }

        return iterate(mapPixels(input.subList(2, input.size)), iterations).sumOf { row ->
            row.filter { it == '1' }.size
        }
    }

    fun part1(input: List<String>): Int {
        return enhanceImage(input, 2)
    }

    fun part2(input: List<String>): Int {
        return enhanceImage(input, 50)
    }

    val sampleInput = readInput("day20_sample")
    val input = readInput("day20")

    check(part1(sampleInput) == 35)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput) == 3351)
    println("Part2: ${part2(input)}")
}