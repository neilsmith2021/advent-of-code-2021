fun main() {

    fun findExplodable(input: String): Int { // Points to the [
        var count = 0
        input.forEachIndexed { index, c ->
            if (c == '[') count++
            if (c == ']') count--
            if (count == 5) return index
        }
        return -1
    }

    fun findSplittable(input: String): Int {
        input.windowed(2).forEachIndexed { index, s ->
            if (s.all { it.isDigit() }) return index
        }
        return -1
    }

    fun findFirstNumberBeforeExplodable(input: String, explodablePosition: Int): Int {
        input.substring(0, explodablePosition).reversed().forEachIndexed { index, c ->
            if (c.isDigit()) {
                return if (input[explodablePosition - index - 2].isDigit()) explodablePosition - index - 2
                else explodablePosition - index - 1
            }
        }
        return -1
    }

    fun findFirstNumberAfterExplodable(input: String, explodableClosePosition: Int): Int {
        input.substring(explodableClosePosition).forEachIndexed { index, c ->
            if (c.isDigit()) return explodableClosePosition + index
        }
        return -1
    }

    fun findDelimiter(input: String, position: Int): Int {
        input.substring(position).forEachIndexed { index, c ->
            if (c == ',' || c == ']') return index + position
        }
        return -1
    }

    fun explode(input: String, openBracketPosition: Int): String {
        val closeBracketPosition = findDelimiter(input, findDelimiter(input, openBracketPosition + 1) + 1)
        val leftNumber = input.substring(openBracketPosition + 1, findDelimiter(input, openBracketPosition)).toInt()
        val rightNumber = input.substring(
            findDelimiter(input, openBracketPosition) + 1, closeBracketPosition
        ).toInt()

        val numberBeforePosition = findFirstNumberBeforeExplodable(input, openBracketPosition)
        val numberAfterPosition = findFirstNumberAfterExplodable(input, closeBracketPosition)

        val numberBefore = if (numberBeforePosition >= 0) input.substring(
            numberBeforePosition, findDelimiter(input, numberBeforePosition)
        ).toInt() else -1
        val numberAfter = if (numberAfterPosition >= 0) input.substring(
            numberAfterPosition, findDelimiter(input, numberAfterPosition)
        ).toInt() else -1

        val stringBefore = if (numberBefore >= 0) "" + input.substring(
            0,
            numberBeforePosition
        ) + "${numberBefore + leftNumber}" + input.substring(
            numberBeforePosition + "$numberBefore".length,
            openBracketPosition
        )
        else input.substring(0, openBracketPosition)

        val stringAfter = if (numberAfter >= 0) "" + input.substring(
            closeBracketPosition + 1,
            numberAfterPosition
        ) + "${numberAfter + rightNumber}" + input.substring(numberAfterPosition + "$numberAfter".length)
        else input.substring(closeBracketPosition + 1)

        return "${stringBefore}0${stringAfter}"
    }

    fun split(input: String, position: Int): String {
        val numberToSplit = input.substring(position, position + 2).toInt()
        val leftNumber = numberToSplit / 2
        val rightNumber = numberToSplit / 2 + numberToSplit % 2
        return input.substring(0, position) + "[$leftNumber,$rightNumber]" + input.substring(position + 2)
    }

    fun reduceSnailFishNumber(input: String): String {
        var stringToReduce = input
        var position = findExplodable(stringToReduce)
        while (position >= 0) {
            stringToReduce = explode(stringToReduce, position)
            position = findExplodable(stringToReduce)
        }

        position = findSplittable(stringToReduce)
        if (position >= 0) {
            stringToReduce = split(stringToReduce, position)
            stringToReduce = reduceSnailFishNumber(stringToReduce)
        }

        return stringToReduce
    }

    fun addSnailFishNumbers(input: List<String>): String {
        return input.reduce { acc, s ->
            reduceSnailFishNumber("[$acc,$s]")
        }
    }

    fun findPairPosition(acc: String): IntRange? {
        val pattern = "\\[[0-9]*,+[0-9]*]".toRegex()
        return pattern.find(acc)?.range
    }

    fun calculateMagnitude(input: String): String {
        tailrec fun collapsePair(acc: String): String {
            val position = findPairPosition(acc)
            return if (position == null) acc.trim('[', ']')
            else {
                val commaPosition = acc.indexOf(',', position.first)
                val left = acc.substring(position.first + 1, commaPosition).toInt()
                val right = acc.substring(commaPosition + 1, position.last).toInt()
                collapsePair(
                    acc.substring(
                        0,
                        position.first
                    ) + (left * 3 + right * 2).toString() + acc.substring(position.last + 1)
                )
            }

        }

        return collapsePair(input)
    }

    fun permutations(n: Int): List<Pair<Int, Int>> {
        return (0 until n).map { left ->
            (0 until n).map { right ->
                Pair(left, right)
            }.filter { it.first != it.second }
        }.flatten()
    }

    fun part1(input: List<String>): Int {
        return calculateMagnitude(addSnailFishNumbers(input)).toInt()
    }

    fun part2(input: List<String>): Int {
        return permutations(input.size).maxOfOrNull { (first, second) ->
            calculateMagnitude(addSnailFishNumbers(listOf(input[first], input[second]))).toInt()
        } ?: 0
    }

    val sampleInput = readInput("day18_sample_1")
    val input = readInput("day18_1")

    check(explode("[[[[[9,8],1],2],3],4]", findExplodable("[[[[[9,8],1],2],3],4]")) == "[[[[0,9],2],3],4]")
    check(explode("[7,[6,[5,[4,[3,2]]]]]", findExplodable("[7,[6,[5,[4,[3,2]]]]]")) == "[7,[6,[5,[7,0]]]]")
    check(explode("[[6,[5,[4,[3,2]]]],1]", findExplodable("[[6,[5,[4,[3,2]]]],1]")) == "[[6,[5,[7,0]]],3]")
    check(
        explode(
            "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]", findExplodable("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]")
        ) == "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"
    )
    check(
        explode(
            "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]", findExplodable("[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]")
        ) == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]"
    )

    check(reduceSnailFishNumber("[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]") == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")

    check(addSnailFishNumbers(sampleInput) == "[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]")
    check(calculateMagnitude("[[[[6,6],[7,6]],[[7,7],[7,0]]],[[[7,7],[7,7]],[[7,8],[9,9]]]]") == "4140")

    check(part1(sampleInput) == 4140)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput) == 3993)
    println("Part2: ${part2(input)}")
}