fun main() {

    fun parseInput(input: List<String>): List<List<String>> {
        return input.map { it.split("|", " ") }
    }

    fun part1(input: List<List<String>>): Int {
        return input.flatMap { it.subList(12, 16) }.count { listOf(2, 3, 4, 7).contains(it.length) }
    }

    fun processEntries(entry: List<String>): Int {
        val outputDigits = entry.subList(12, 16).map { it.toCharArray().sorted().joinToString("") }

        val one = entry.subList(0, 10).first { it.length == 2 }.toCharArray().sorted().joinToString("")
        val seven = entry.subList(0, 10).first { it.length == 3 }.toCharArray().sorted().joinToString("")
        val four = entry.subList(0, 10).first { it.length == 4 }.toCharArray().sorted().joinToString("")
        val eight = entry.subList(0, 10).first { it.length == 7 }.toCharArray().sorted().joinToString("")

        val fiveCharacters =
            entry.subList(0, 10).filter { it.length == 5 }.map { it.toCharArray().sorted().joinToString("") }
        val sixCharacters =
            entry.subList(0, 10).filter { it.length == 6 }.map { it.toCharArray().sorted().joinToString("") }

        val six = sixCharacters.first {
            !it.contains(one.first()) || !it.contains(one.last())
        }

        val three = fiveCharacters.first {
            it.contains(one.first()) && it.contains(one.last())
        }

        val nine = sixCharacters.filter { it != six }.first { sixCharacterString ->
            three.count { sixCharacterString.contains(it) } == 5
        }

        val zero = sixCharacters.first { it != six && it != nine }

        val five = fiveCharacters.filter { it != three }.first { fiveCharacterString ->
            nine.count { fiveCharacterString.contains(it) } == 5
        }

        val two = fiveCharacters.first { it != three && it != five }

        val segments = listOf(zero, one, two, three, four, five, six, seven, eight, nine)

        return outputDigits.joinToString("") { segments.indexOf(it).toString() }.toInt()
    }

    fun part2(input: List<List<String>>): Int {
        return input.sumOf { processEntries(it) }
    }

    val sampleInput = readInput("day08_sample_1")
    val input = readInput("day08_1")

    check(part1(parseInput(sampleInput)) == 26)
    println("Part1: ${part1(parseInput(input))}")

    check(part2(parseInput(sampleInput)) == 61229)
    println("Part2: ${part2(parseInput(input))}")
}