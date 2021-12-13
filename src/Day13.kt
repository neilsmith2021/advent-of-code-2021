fun main() {

    fun getCoordinates(input: List<String>): List<Pair<Int, Int>> {
        return input.filter { it.isNotEmpty() && it[0].isDigit() }
            .map {
                val points = it.split(",")
                Pair(points[0].toInt(), points[1].toInt())
            }
    }

    val foldX: (List<Pair<Int, Int>>, Int) -> List<Pair<Int, Int>> = { coordinates, foldLine ->
        coordinates.map { (x, y) ->
            Pair(if (x < foldLine) x else foldLine - (x - foldLine), y)
        }.distinct()
    }

    val foldY: (List<Pair<Int, Int>>, Int) -> List<Pair<Int, Int>> = { coordinates, foldLine ->
        coordinates.map { (x, y) ->
            Pair(x, if (y < foldLine) y else foldLine - (y - foldLine))
        }.distinct()
    }

    fun getFoldFunctions(input: List<String>): List<Pair<Int, (List<Pair<Int, Int>>, Int) -> List<Pair<Int, Int>>>> {
        return input.filter { it.startsWith("fold") }
            .map {
                val splitLine = it.split(" ", "=")
                if (splitLine[2] == "x") Pair(splitLine[3].toInt(), foldX) else Pair(splitLine[3].toInt(), foldY)
            }
    }

    fun part1(input: List<String>): Int {
        val coordinates = getCoordinates(input)
        val folds = getFoldFunctions(input)

        return folds[0].second.invoke(coordinates, folds[0].first).count()
    }

    fun printGrid(input: List<Pair<Int, Int>>) {
        val xSize = input.maxOf { it.first + 1 }
        val ySize = input.maxOf { it.second + 1 }

        val grid: Array<CharArray> = Array(ySize) { CharArray(xSize) { ' ' } }

        input.forEach { (x, y) ->
            grid[y][x] = '#'
        }

        grid.forEach { println(it) }
    }

    fun part2(input: List<String>) {
        val coordinates = getCoordinates(input)
        val folds = getFoldFunctions(input)

        val coordinatesAfterFolding = folds.fold(coordinates) { acc, next -> next.second.invoke(acc, next.first) }
        printGrid(coordinatesAfterFolding)
    }

    val sampleInput = readInput("day13_sample_1")
    val input = readInput("day13_1")

    check(part1(sampleInput) == 17)
    println("Part1: ${part1(input)}")

    println("Part2 Sample:")
    part2(sampleInput)
    println("\nPart2 Real:")
    part2(input)
}