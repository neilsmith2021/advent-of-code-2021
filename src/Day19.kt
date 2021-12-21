import java.util.*
import kotlin.math.abs

fun main() {

    fun mapInput(input: List<String>): List<List<Triple<Int, Int, Int>>> {
        val indexes = input.mapIndexed { index, s ->
            if (s.startsWith("---")) index + 1 else 0
        }.filter { it > 0 }
        val scanners = indexes.windowed(2, partialWindows = true).map { index ->
            input.subList(index[0], if (index.size > 1) index[1] - 2 else input.size)
        }
        return scanners.map { scanner ->
            scanner.map { xyz ->
                val numbers = xyz.split(',').map { n -> n.toInt() }
                Triple(numbers[0], numbers[1], numbers[2])
            }
        }
    }

    // Returns 24 rotations of the point based on x,y,z rotations
    fun rotations(): List<Triple<Int, Int, Int>> {
        val point = Triple(1, 2, 3)
        val sin90 = 1
        val cos90 = 0
        val rotateX = arrayOf(intArrayOf(1, 0, 0), intArrayOf(0, cos90, -sin90), intArrayOf(0, sin90, cos90))
        val rotateY = arrayOf(intArrayOf(cos90, 0, sin90), intArrayOf(0, 1, 0), intArrayOf(-sin90, 0, cos90))
        val rotateZ = arrayOf(intArrayOf(cos90, -sin90, 0), intArrayOf(sin90, cos90, 0), intArrayOf(0, 0, 1))

        fun rotateOnAxis(point: Triple<Int, Int, Int>, matrix: Array<IntArray>): List<Triple<Int, Int, Int>> {
            val points = mutableListOf<Triple<Int, Int, Int>>()
            (0 until 4).fold(point) { acc, _ ->
                points.add(acc)
                Triple(
                    acc.first * matrix[0][0] + acc.second * matrix[0][1] + acc.third * matrix[0][2],
                    acc.first * matrix[1][0] + acc.second * matrix[1][1] + acc.third * matrix[1][2],
                    acc.first * matrix[2][0] + acc.second * matrix[2][1] + acc.third * matrix[2][2],
                )
            }
            return points
        }

        val xRotations = rotateOnAxis(point, rotateX)
        val yRotations = xRotations.flatMap { rotateOnAxis(it, rotateY) }
        val zRotations = yRotations.flatMap { rotateOnAxis(it, rotateZ) }

        return zRotations.distinct()
    }

    fun permutations(firstScannerSize: Int, secondScannerSize: Int): List<Pair<Int, Int>> {
        return (0 until firstScannerSize).map { left ->
            (0 until secondScannerSize).map { right ->
                Pair(left, right)
            }
        }.flatten()
    }

    // Takes a point, returns 24 variations of the point based on x,y,z rotations
    fun orientations(
        point: Triple<Int, Int, Int>,
        rotations: List<Triple<Int, Int, Int>>
    ): List<Triple<Int, Int, Int>> {
        val p = point.toList()
        return rotations.map { (x, y, z) ->
            Triple(p[abs(x) - 1] * x.polarity(), p[abs(y) - 1] * y.polarity(), p[abs(z) - 1] * z.polarity())
        }
    }

    fun singleOrientationMatches(
        fromScanner: List<Triple<Int, Int, Int>>,  // List of points from a scanner in a specific rotation
        toScannerOrientations: List<List<Triple<Int, Int, Int>>>  // List<Orientations<Points>>
    ): Pair<Int, Triple<Int, Int, Int>>? {  // Returns the offset of toScanner from fromScanner if 12 matching points, or null
        toScannerOrientations.forEachIndexed { index, rotation ->
            val permutations = permutations(fromScanner.size, rotation.size)
            val differencesMap = permutations.map { (from, to) ->
                Triple(
                    fromScanner[from].first - (rotation[to].first),
                    fromScanner[from].second - rotation[to].second,
                    fromScanner[from].third - rotation[to].third,
                )
            }
            val differences = differencesMap.groupingBy { it }.eachCount().filter { it.value >= 12 }.keys
            if (differences.isNotEmpty()) return Pair(index, differences.first())
        }
        return null
    }

    fun processBeaconReports(input: List<String>): Pair<List<List<List<Triple<Int, Int, Int>>>>, MutableMap<Int, Pair<Int, Triple<Int, Int, Int>>>> {
        val scanners = mapInput(input)
        val rotations = rotations()

        val scannerPoints = scanners.map { points -> points.map { orientations(it, rotations) }.transpose() }

        val offsets = mutableMapOf<Int, Pair<Int, Triple<Int, Int, Int>>>()
        offsets[0] = Pair(0, Triple(0, 0, 0))

        val q: Queue<Int> = LinkedList()
        q.add(0)
        while (q.size > 0) {
            val nextSensorToCheck = q.remove()
            for (scanner in scannerPoints.indices) {
                if (offsets.containsKey(scanner)) continue
                val thisSensor = offsets[nextSensorToCheck]!!
                singleOrientationMatches(
                    scannerPoints[nextSensorToCheck][thisSensor.first],
                    scannerPoints[scanner]
                )?.let {
                    offsets[scanner] = Pair(
                        it.first,
                        Triple(
                            it.second.first + thisSensor.second.first,
                            it.second.second + thisSensor.second.second,
                            it.second.third + thisSensor.second.third
                        )
                    )
                    q.add(scanner)
                }
            }
        }
        return Pair(scannerPoints, offsets)
    }

    fun part1(input: List<String>): Int {
        val (scannerPoints, offsets) = processBeaconReports(input)
        val beacons = mutableSetOf<Triple<Int, Int, Int>>()
        scannerPoints.forEachIndexed { index, lists ->
            val orientation = offsets[index]!!.first
            val offset = offsets[index]!!.second
            beacons.addAll(lists[orientation].map {
                Triple(
                    it.first + offset.first,
                    it.second + offset.second,
                    it.third + offset.third
                )
            })
        }

        return beacons.size
    }

    fun part2(input: List<String>): Int {

        fun manhattanDistance(from: Triple<Int, Int, Int>, to: Triple<Int, Int, Int>): Int {
            return abs(from.first - to.first) +
                    abs(from.second - to.second) +
                    abs(from.third - to.third)
        }

        val (_, offsets) = processBeaconReports(input)
        val scannerLocations = offsets.values.map { it.second }

        val distances = mutableListOf<Int>()
        (0 until scannerLocations.size - 1).forEach { fromLocation ->
            (fromLocation until scannerLocations.size).forEach { to ->
                distances.add(manhattanDistance(scannerLocations[fromLocation], scannerLocations[to]))
            }
        }

        return distances.maxOrNull() ?: 0
    }

    val sampleInput = readInput("day19_sample_1")
    val input = readInput("day19_1")

    check(part1(sampleInput) == 79)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput) == 3621)
    println("Part2: ${part2(input)}")
}