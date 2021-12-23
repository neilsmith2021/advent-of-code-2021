import kotlin.math.max
import kotlin.math.min

fun main() {

    fun parseInput(input: List<String>): List<Pair<Boolean, Triple<IntRange, IntRange, IntRange>>> {
        return input.map { line ->
            val splits = line.split(" ", "=", "..", ",")
            Pair(
                splits[0] == "on", Triple(
                    splits[2].toInt()..splits[3].toInt(),
                    splits[5].toInt()..splits[6].toInt(),
                    splits[8].toInt()..splits[9].toInt()
                )
            )
        }
    }

    fun getCubes(ranges: Triple<IntRange, IntRange, IntRange>): List<Triple<Int, Int, Int>> {
        return buildList {
            for (x in ranges.first) for (y in ranges.second) for (z in ranges.third) add(Triple(x, y, z))

        }
    }

    fun part1(input: List<String>): Int {
        val steps = parseInput(input).filter { it.second.first.first >= -50 && it.second.first.last <= 50 }
        val reactor = mutableSetOf<Triple<Int, Int, Int>>()

        steps.forEach { step ->
            val cubes = getCubes(step.second)
            if (step.first) reactor.addAll(cubes) else reactor.removeAll(cubes.toSet())
        }

        return reactor.size
    }

    fun cubesOverlapping(
        cube1: Triple<IntRange, IntRange, IntRange>, cube2: Triple<IntRange, IntRange, IntRange>
    ): Boolean {
        return (cube1.first.contains(cube2.first.first) || cube2.first.contains(cube1.first.first)) &&
                (cube1.second.contains(cube2.second.first) || cube2.second.contains(cube1.second.first)) &&
                (cube1.third.contains(cube2.third.first) || cube2.third.contains(cube1.third.first))
    }

    fun cubeUnion(
        cube1: Triple<IntRange, IntRange, IntRange>, cube2: Triple<IntRange, IntRange, IntRange>
    ): Triple<IntRange, IntRange, IntRange> {
        return Triple(
            IntRange(
                max(cube1.first.first, cube2.first.first),
                min(cube1.first.last, cube2.first.last),
            ), IntRange(
                max(cube1.second.first, cube2.second.first),
                min(cube1.second.last, cube2.second.last),
            ), IntRange(
                max(cube1.third.first, cube2.third.first),
                min(cube1.third.last, cube2.third.last),
            )
        )
    }

    fun cubeSize(cube: Triple<IntRange, IntRange, IntRange>): Long {
        return cube.first.count().toLong() * cube.second.count() * cube.third.count()
    }

    fun part2(input: List<String>): Long {
        val steps = parseInput(input)
        val reactor = mutableListOf<Pair<Boolean, Triple<IntRange, IntRange, IntRange>>>()

        steps.forEach { (isTurningOn, cube) ->
            val newCubes = mutableListOf<Pair<Boolean, Triple<IntRange, IntRange, IntRange>>>()
            reactor.forEach { (reactorCubeTurningOn, reactorCube) ->
                if (cubesOverlapping(cube, reactorCube)) {
                    newCubes.add(
                        Pair(
                            !reactorCubeTurningOn, cubeUnion(cube, reactorCube)
                        )
                    )
                }
            }
            reactor.addAll(newCubes)
            if (isTurningOn) reactor.add(Pair(true, cube))
        }

        val size = reactor.fold(0L) { acc, (isTurningOn, cube) ->
            acc + (cubeSize(cube) * if (isTurningOn) 1L else -1L)
        }

        return size
    }

    val sampleInput1 = readInput("day22_sample_1")
    val sampleInput2 = readInput("day22_sample_2")
    val input = readInput("day22")

    check(part1(sampleInput1) == 590784)
    println("Part1: ${part1(input)}")


    check(part2(sampleInput2) == 2758514936282235)
    println("Part2: ${part2(input)}")
}