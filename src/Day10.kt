fun main() {

    fun validClosingCharacter(closing: Char, opening: Char): Boolean {
        return ((opening == '(' && closing == ')') ||
                (opening == '[' && closing == ']') ||
                (opening == '<' && closing == '>') ||
                (opening == '{' && closing == '}'))
    }

    fun part1(input: List<CharArray>): Int {

        val incorrectClosingCharacters = mutableListOf<Char>()

        input.forEach { line ->
            val stack = MutableStack<Char>()
            line.forEach inner@{
                if ("([{<".contains(it)) {
                    stack.push(it)
                } else {
                    val topOfStack = stack.pop()
                    if (!validClosingCharacter(it, topOfStack)) {
                        incorrectClosingCharacters.add(it)
                        return@inner
                    }
                }
            }
        }

        return incorrectClosingCharacters.map {
            when (it) {
                ')' -> 3
                ']' -> 57
                '}' -> 1197
                else -> 25137
            }
        }.sum()
    }

    fun part2(input: List<CharArray>): Long {

        val stacks = mutableListOf<MutableStack<Char>>()

        input.forEach { line ->
            val stack = MutableStack<Char>()
            var valid = true
            line.forEach inner@{
                if ("([{<".contains(it)) {
                    stack.push(it)
                } else {
                    val topOfStack = stack.pop()
                    if (!validClosingCharacter(it, topOfStack)) {
                        valid = false
                        return@inner
                    }
                }
            }
            if (valid) stacks.add(stack)
        }

        val requiredClosingCharacters: MutableList<MutableList<Int>> = mutableListOf()

        stacks.forEach { stack ->
            val closingValues = mutableListOf<Int>()
            while (!stack.isEmpty()) {
                val topItem = stack.pop()
                closingValues.add(
                    when (topItem) {
                        '(' -> 1
                        '[' -> 2
                        '{' -> 3
                        else -> 4
                    }
                )
            }
            requiredClosingCharacters.add(closingValues)
        }

        val closingScores = requiredClosingCharacters.map { closingValues ->
            closingValues.fold(0L) { acc, closingValue ->
                acc * 5 + closingValue
            }
        }.sorted()

        return closingScores[closingScores.size / 2]
    }

    val sampleInput = readInput("day10_sample_1").map { it.toCharArray() }
    val input = readInput("day10_1").map { it.toCharArray() }

    check(part1(sampleInput) == 26397)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput) == 288957L)
    println("Part2: ${part2(input)}")
}