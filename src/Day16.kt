fun main() {

    fun mapHexStringToBinaryString(input: String) =
        input.toCharArray().joinToString("") { it.digitToInt(16).toString(2).padStart(4, '0') }

    fun decodePackets(bits: String, bitTokens: MutableStack<Long>): Int {

        var sumOfPacketVersions = 0

        fun parseBits(bits: String): Int {

            fun parseLiteralValue(bits: String): Int {
                val literals = mutableListOf<String>()
                val version = bits.substring(0, 3).toInt(2)
                sumOfPacketVersions += version
                var charactersConsumed = 6
                while (true) {
                    val group = bits.substring(charactersConsumed, charactersConsumed + 5)
                    charactersConsumed += 5
                    literals.add(group.substring(1).toInt(2).toString(16))
                    if (group[0] == '0') break
                }
                bitTokens.push(literals.joinToString("").toLong(16))
                return literals.size * 5 + 6
            }

            fun parseOperator(bits: String): Int {
                var charactersConsumed = 0
                val version = bits.substring(0, 3).toInt(2)
                sumOfPacketVersions += version
                val type = bits.substring(3, 6).toInt(2)
                val lengthType = bits[6]
                var childPacketCount = 0
                if (lengthType == '0') {
                    val subPacketLength = bits.substring(7, 22).toInt(2)
                    while (charactersConsumed < subPacketLength) {
                        charactersConsumed += parseBits(bits.substring(22 + charactersConsumed))
                        childPacketCount++
                    }
                } else {
                    val subPacketLength = bits.substring(7, 18).toInt(2)
                    repeat(subPacketLength) {
                        charactersConsumed += parseBits(bits.substring(18 + charactersConsumed))
                    }
                    childPacketCount += subPacketLength
                }

                val tokens = List(childPacketCount) { bitTokens.pop() }
                when (type) {
                    0 -> bitTokens.push(tokens.sumOf { it })
                    1 -> bitTokens.push(tokens.fold(1) { acc, v -> acc * v })
                    2 -> bitTokens.push(tokens.minOfOrNull { it } ?: 0)
                    3 -> bitTokens.push(tokens.maxOfOrNull { it } ?: 0)
                    5 -> bitTokens.push(if (tokens[0] < tokens[1]) 1 else 0)
                    6 -> bitTokens.push(if (tokens[0] > tokens[1]) 1 else 0)
                    7 -> bitTokens.push(if (tokens[0] == tokens[1]) 1 else 0)
                }

                return charactersConsumed + if (lengthType == '0') 22 else 18
            }

            return if (bits.substring(3, 6).toInt(2) == 4) {
                parseLiteralValue(bits)
            } else {
                parseOperator(bits)
            }
        }

        parseBits(bits)
        return sumOfPacketVersions
    }

    fun part1(bits: String): Int {
        val bitTokens: MutableStack<Long> = MutableStack()
        return decodePackets(bits, bitTokens)
    }

    fun part2(bits: String): Long {
        val bitTokens: MutableStack<Long> = MutableStack()
        decodePackets(bits, bitTokens)
        return bitTokens.pop()
    }

    val sampleInput = readInput("day16_sample_1")
    val input = readInput("day16_1")

    check(mapHexStringToBinaryString(sampleInput[0]) == "110100101111111000101000")
    check(mapHexStringToBinaryString(sampleInput[1]) == "00111000000000000110111101000101001010010001001000000000")
    check(mapHexStringToBinaryString(sampleInput[2]) == "11101110000000001101010000001100100000100011000001100000")

    check(part1(mapHexStringToBinaryString(sampleInput[0])) == 6)
    check(part1(mapHexStringToBinaryString(sampleInput[1])) == 9)
    check(part1(mapHexStringToBinaryString(sampleInput[2])) == 14)
    check(part1(mapHexStringToBinaryString(sampleInput[3])) == 16)
    check(part1(mapHexStringToBinaryString(sampleInput[4])) == 12)
    check(part1(mapHexStringToBinaryString(sampleInput[5])) == 23)
    check(part1(mapHexStringToBinaryString(sampleInput[6])) == 31)

    println("Part1: ${part1(mapHexStringToBinaryString(input[0]))}")

    check(part2(mapHexStringToBinaryString(sampleInput[8])) == 3L)
    check(part2(mapHexStringToBinaryString(sampleInput[9])) == 54L)
    check(part2(mapHexStringToBinaryString(sampleInput[10])) == 7L)
    check(part2(mapHexStringToBinaryString(sampleInput[11])) == 9L)
    check(part2(mapHexStringToBinaryString(sampleInput[12])) == 1L)
    check(part2(mapHexStringToBinaryString(sampleInput[13])) == 0L)
    check(part2(mapHexStringToBinaryString(sampleInput[14])) == 0L)
    check(part2(mapHexStringToBinaryString(sampleInput[15])) == 1L)

    println("Part2: ${part2(mapHexStringToBinaryString(input[0]))}")
}