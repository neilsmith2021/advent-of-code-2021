fun main() {

    fun part1(input: List<String>): Int {
        val binaryNumberSize = input[0].length

        val gammaRateBinary = input.fold(List(binaryNumberSize) { 0 }) { acc, s ->
            val bitsAsInts = s.toCharArray().map { it.digitToInt() }
            List(s.length) { index -> acc[index] + bitsAsInts[index] }
        }.map { if (it > input.size / 2) '1' else '0' }

        val epsilonRateBinary = gammaRateBinary.map { if (it == '0') '1' else '0' }

        val gammaRate = gammaRateBinary.joinToString("").toInt(2)
        val epsilonRate = epsilonRateBinary.joinToString("").toInt(2)

        return gammaRate * epsilonRate
    }

    fun part2(input: List<String>): Int {
        tailrec fun filterGas(index: Int, tail: List<String>, compareFunction: (Int, Int) -> Boolean): String {
            return if (tail.size == 1) {
                tail[0]
            } else {
                val numberOfOnes = tail.count { it[index] == '1' }
                filterGas(
                    index + 1,
                    tail.filter { it[index] == if (compareFunction(numberOfOnes, tail.size)) '1' else '0' },
                    compareFunction
                )
            }
        }

        val oxygen = filterGas(0, input) { x, y -> x * 2 >= y }.toInt(2)
        val co2 = filterGas(0, input) { x, y -> x * 2 < y }.toInt(2)

        return oxygen * co2
    }

//    val input = readInput("day03_sample_1")
    val input = readInput("day03_1")

    println(part1(input))
    println(part2(input))
}