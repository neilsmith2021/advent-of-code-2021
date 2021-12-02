fun main() {
    fun part1(input: List<Int>): Int {

        tailrec fun countIncreases(currentCount: Int, head: Int, tail: List<Int>): Int {
            return if (tail.isEmpty()) {
                currentCount
            } else {
                countIncreases(currentCount + if (head < tail.first()) 1 else 0, tail.first(), tail.drop(1))
            }
        }
        return countIncreases(0, input.first(), input.drop(1))

    }

    fun part2(input: List<Int>): Int {
        tailrec fun countWindowedIncreases(currentCount: Int, head: List<Int>, tail: List<Int>): Int {
            return if (tail.size < 3) {
                currentCount
            } else {
                countWindowedIncreases(
                    currentCount + if (head.sum() < tail.subList(0, 3).sum()) 1 else 0,
                    tail.subList(0, 3), tail.drop(1)
                )
            }
        }
        return countWindowedIncreases(0, input.subList(0, 3), input.drop(1))
}

//    val input = readInput("day01_sample_1").map { it.toInt() }
    val input = readInput("day01_1").map { it.toInt() }
    println(part1(input))
    println(part2(input))
}
