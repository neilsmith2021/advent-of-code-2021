fun main() {

    fun parseInput(input: List<String>): Array<CharArray> {
        return Array(input.size) {
            input[it].toCharArray()
        }
    }

    fun part1(seaFloor: Array<CharArray>): Int {
        val width = seaFloor[0].lastIndex
        val height = seaFloor.lastIndex

        var iterations = 0

        while (true) {
            val cellsThatCanMoveRight = seaFloor.flatMapIndexed() { yIndex, y ->
                y.mapIndexed { xIndex, x ->
                    if (x == '>' && ((xIndex < width && y[xIndex + 1] == '.') || (xIndex == width && y[0] == '.')))
                        Pair(Pair(xIndex, yIndex), Pair(if (xIndex == width) 0 else xIndex + 1, yIndex))
                    else null
                }.filterNotNull()
            }

            cellsThatCanMoveRight.forEach { (from, to) ->
                seaFloor[from.second][from.first] = '.'
                seaFloor[to.second][to.first] = '>'
            }

            val cellsThatCanMoveDown = seaFloor.flatMapIndexed { yIndex: Int, y: CharArray ->
                y.mapIndexed { xIndex, x ->
                    if (x == 'v' && ((yIndex < height && seaFloor[yIndex + 1][xIndex] == '.') || (yIndex == height) && seaFloor[0][xIndex] == '.'))
                        Pair(Pair(xIndex, yIndex), Pair(xIndex, if (yIndex == height) 0 else yIndex + 1))
                    else null
                }.filterNotNull()
            }

            cellsThatCanMoveDown.forEach { (from, to) ->
                seaFloor[from.second][from.first] = '.'
                seaFloor[to.second][to.first] = 'v'
            }

            iterations++
            if (cellsThatCanMoveRight.isEmpty() && cellsThatCanMoveDown.isEmpty()) break
        }

        return iterations
    }

    val sampleInput = parseInput(readInput("day25_sample"))
    val realInput = parseInput(readInput("day25"))

    check(part1(sampleInput) == 58)
    println("Part1: ${part1(realInput)}")
}