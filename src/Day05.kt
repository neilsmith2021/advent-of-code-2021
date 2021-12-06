import LineType.DiagonalLine
import LineType.StraightLine
import java.lang.Integer.max
import kotlin.math.abs

sealed class LineType {
    object StraightLine : LineType()
    object DiagonalLine : LineType()
}

fun main() {
    data class Coord(val x: Int, val y: Int)

    class Line(line: List<Int>) {
        val startCoord: Coord
        val endCoord: Coord
        val xIncrement: Int
        val yIncrement: Int
        val lineType: LineType

        init {
            startCoord = Coord(line[0], line[1])
            endCoord = Coord(line[2], line[3])
            xIncrement = getIncrement(startCoord.x, endCoord.x)
            yIncrement = getIncrement(startCoord.y, endCoord.y)
            lineType =
                if (startCoord.x == endCoord.x || startCoord.y == endCoord.y) StraightLine else DiagonalLine
        }

        private fun getIncrement(start: Int, end: Int): Int {
            return if (start < end) 1
            else if (start > end) -1
            else 0
        }
    }

    fun mapInput(input: List<String>): List<List<Int>> {
        return input.map { line ->
            line.split(",", " -> ")
        }.map { points ->
            listOf(points[0].toInt(), points[1].toInt(), points[2].toInt(), points[3].toInt())
        }
    }

    fun countCommonPoints(grid: Array<IntArray>): Int {
        val countRows: (Int) -> Int = { x -> if (x >= 2) 1 else 0 }
        val count = grid.sumOf { rows ->
            rows.sumOf { cell ->
                countRows(cell)
            }
        }

        return count
    }

    fun plotLines(input: List<Line>, grid: Array<IntArray>) {
        input.forEach { line ->
            var x = line.startCoord.x
            var y = line.startCoord.y
            val lineLength = max(
                abs(line.startCoord.x - line.endCoord.x),
                abs(line.startCoord.y - line.endCoord.y)
            )
            repeat(lineLength + 1) {
                grid[x][y] += 1
                x += line.xIncrement
                y += line.yIncrement
            }
        }
    }

    fun getGrid(input: List<List<Int>>): Array<IntArray> {
        val maxX = max(
            input.maxOf { it[0] },
            input.maxOf { it[2] }
        )

        val maxY = max(
            input.maxOf { it[1] },
            input.maxOf { it[3] }
        )

        return Array(maxX + 1) { IntArray(maxY + 1) { 0 } }
    }

//    val input = readInput("day05_sample_1")
    val input = readInput("day05_1")

    val mappedInput = mapInput(input)
    val grid = getGrid(mappedInput)
    val lines = mappedInput.map { Line(it) }

    plotLines(lines.filter { it.lineType == StraightLine }, grid)
    println("part 1: ${countCommonPoints(grid)}")
    plotLines(lines.filter { it.lineType == DiagonalLine }, grid)
    println("part 2: ${countCommonPoints(grid)}")
}