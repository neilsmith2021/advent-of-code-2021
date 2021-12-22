import java.lang.Long.max

fun main() {

    fun parseInput(input: List<String>): List<Int> {
        return input.map { it.split(":").last().trim().toInt() }
    }

    fun updatePosition(playerPosition: Int, nextRollAmount: Int): Int {
        return (playerPosition - 1 + nextRollAmount) % 10 + 1
    }

    fun playGame(
        player1StartPosition: Int, player2StartPosition: Int, dice: DeterministicDice, winningScore: Int
    ): MutableList<Pair<Int, Int>> {
        val players = mutableListOf(Pair(player1StartPosition, 0), Pair(player2StartPosition, 0))

        tailrec fun takeTurn(playerToMove: Int) {
            val nextRollAmount = dice.rollDice()
            val playerPosition = players[playerToMove].first
            val playerScore = players[playerToMove].second
            val newPosition = updatePosition(playerPosition, nextRollAmount)
            players[playerToMove] = Pair(newPosition, playerScore + newPosition)
            if (playerScore + newPosition < winningScore) takeTurn((playerToMove + 1) % 2)
        }

        takeTurn(0)

        return players
    }

    fun part1(input: List<String>): Int {
        val players = parseInput(input)
        val dice = DeterministicDice()
        return playGame(players[0], players[1], dice, 1000).minOf { it.second } * dice.getNumberOfTimesRolled()
    }

    fun part2(input: List<String>): Long {
        val players = parseInput(input)

        val cache = mutableMapOf(Game(Player(players[0]), Player(players[1]), 1) to 1L)
        val diceOutcomes = mapOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)

        fun rollDice(gameInProgress: Game): List<Pair<Game, Int>> {
            return diceOutcomes.map { (rollAmount, frequency) ->
                if (gameInProgress.nextPlayerToRoll == 1) {
                    Pair(
                        Game(
                            gameInProgress.player1.move(rollAmount),
                            gameInProgress.player2.copy(),
                            2
                        ), frequency
                    )
                } else {
                    Pair(
                        Game(
                            gameInProgress.player1.copy(),
                            gameInProgress.player2.move(rollAmount),
                           1
                        ), frequency
                    )
                }
            }
        }

        tailrec fun runGameRound() {
            val currentGame = cache.filter { it.key.isGameInProgress() }.minByOrNull { it.key.gameValue() }

            if (currentGame != null) {
                val grouped = rollDice(currentGame.key).groupingBy { it }.eachCount()
                grouped.forEach { (game, count) ->
                    val cachedVersion = cache.getOrDefault(game.first, 0L)
                    cache[game.first] = cachedVersion + (currentGame.value * count * game.second)
                }
                cache.remove(currentGame.key)
                runGameRound()
            }
        }

        runGameRound()

        val player1Wins = cache.filter { it.key.player1.score >= 21 }.values.sum()
        val player2Wins = cache.filter { it.key.player2.score >= 21 }.values.sum()

        return max(player1Wins, player2Wins)
    }

    val sampleInput = readInput("day21_sample")
    val input = readInput("day21")

    check(part1(sampleInput) == 739785)
    println("Part1: ${part1(input)}")

    check(part2(sampleInput) == 444356092776315)
    println("Part2: ${part2(input)}")
}

data class Player(
    val boardPosition: Int, val score: Int = 0
) {
    fun move(numberOfSquaresToMove: Int): Player {
        val newPosition = (boardPosition - 1 + numberOfSquaresToMove) % 10 + 1
        return Player(newPosition, score + newPosition)
    }
}

data class Game(
    val player1: Player, val player2: Player, val nextPlayerToRoll: Int
) {
    fun isGameInProgress() = player1.score < 21 && player2.score < 21
    fun gameValue() = player1.score + player2.score
}

class DeterministicDice {
    private var numberOfTimesRolled = 0
    private var lastNumberRolled = 100

    fun getNumberOfTimesRolled() = numberOfTimesRolled

    fun rollDice(): Int {
        tailrec fun rollDice(acc: Int, remainingNumberOfRolls: Int): Int {
            return if (remainingNumberOfRolls == 0) acc
            else {
                val nextDiceRoll = if (lastNumberRolled == 100) 1 else lastNumberRolled + 1
                lastNumberRolled = nextDiceRoll
                numberOfTimesRolled++
                rollDice(acc + nextDiceRoll, remainingNumberOfRolls - 1)
            }
        }
        return rollDice(0, 3)
    }
}