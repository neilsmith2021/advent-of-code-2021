data class BingoCard(val numbers: List<Int>) {

    private val mask = BooleanArray(25) { false }
    private var completionTurn = -1
    private var winningNumber = -1

    fun nextNumber(value: Int): Boolean {
        updateMask(value)
        completionTurn++
        return if (isCardComplete()) {
            winningNumber = value
            true
        } else {
            false
        }
    }

    fun cardCount(): Int {
        return numbers.foldIndexed(0) { index, acc, i ->
            acc + if (!mask[index]) i else 0
        }
    }

    fun getCompletionTurn() = completionTurn

    fun getWinningNumber() = winningNumber

    private fun updateMask(value: Int) {
        val positionOfNumber = numbers.indexOf(value)
        if (positionOfNumber >= 0) {
            mask[positionOfNumber] = true
        }
    }

    private fun isCardComplete(): Boolean {
        val isCardComplete = positionsToCheck.firstOrNull { lineToCheck ->
            lineToCheck.none { !mask[it] }
        }
        return isCardComplete != null
    }

    companion object {

        fun from(input: List<String>): BingoCard {
            val numbers = input.flatMap { line -> line.trim().replace("  ", " ").split(' ').map { it.toInt() } }
            return BingoCard(numbers)
        }

        val positionsToCheck = listOf(
            listOf(0, 1, 2, 3, 4),
            listOf(5, 6, 7, 8, 9),
            listOf(10, 11, 12, 13, 14),
            listOf(15, 16, 17, 18, 19),
            listOf(20, 21, 22, 23, 24),
            listOf(0, 5, 10, 15, 20),
            listOf(1, 6, 11, 16, 21),
            listOf(2, 7, 12, 17, 22),
            listOf(3, 8, 13, 18, 23),
            listOf(4, 9, 14, 19, 24),
        )
    }
}

fun main() {

    fun mapInput(input: List<String>): List<BingoCard> {
        val calledNumbers = input[0].split(',').map { it.toInt() }
        val bingoCards = mutableListOf<BingoCard>()
        (2..input.size - 5 step 6).forEach { bingoCards.add(BingoCard.from(input.subList(it, it + 5))) }

        bingoCards.forEach bingo@{ bingoCard ->
            calledNumbers.forEach { calledNumber ->
                if (bingoCard.nextNumber(calledNumber)) {
                    return@bingo
                }
            }
        }

        return bingoCards
    }

    fun part1(bingoCards: List<BingoCard>): Int {
        val winningCard = bingoCards.minByOrNull { it.getCompletionTurn() }
        return winningCard?.let { it.cardCount() * it.getWinningNumber() } ?: 0
    }

    fun part2(bingoCards: List<BingoCard>): Int {
        val losingCard = bingoCards.maxByOrNull { it.getCompletionTurn() }
        return losingCard?.let { it.cardCount() * it.getWinningNumber() } ?: 0
    }

//    val input = readInput("day04_sample_1")
    val input = readInput("day04_1")
    val bingCards = mapInput(input)

    println("Part 1 - Winning card score ${part1(bingCards)}")
    println("Part 2 - Losing card score ${part2(bingCards)}")
}