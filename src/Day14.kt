fun main() {

    fun getInsertionRules(input: List<String>): Map<String, Char> {
        return input.drop(2).associate {
            it.substring(0, 2) to it.last()
        }
    }

    fun processPolymers(template: String, rules: Map<String, Char>, iterations: Int): Long {
        val letterCounts = mutableMapOf<Char, Long>()
        val ruleCounts: MutableMap<String, Long> = mutableMapOf()
        rules.keys.associateWithTo(ruleCounts) { 0L }

        template.split("").subList(1, template.length + 1).windowed(2) {
            ruleCounts["${it[0]}${it[1]}"] = ruleCounts["${it[0]}${it[1]}"]!! + 1
        }

        template.forEach { letter ->
            letterCounts[letter] = (letterCounts[letter] ?: 0L) + 1
        }

        tailrec fun processIterations(iteration: Int, stepCounts: Map<String, Long>) {
            if (iteration == 0) return
            else {
                val newRuleCounts = mutableMapOf<String, Long>()
                newRuleCounts.putAll(stepCounts)
                stepCounts.forEach { (rule, count) ->
                    if (count > 0) {
                        val newLetter = rules[rule]!!
                        letterCounts[newLetter] = (letterCounts[newLetter] ?: 0L) + count
                        newRuleCounts[rule] = newRuleCounts[rule]!! - count
                        newRuleCounts["${rule[0]}$newLetter"] = newRuleCounts["${rule[0]}$newLetter"]!! + count
                        newRuleCounts["$newLetter${rule[1]}"] = newRuleCounts["$newLetter${rule[1]}"]!! + count
                    }
                }
                processIterations(iteration - 1, newRuleCounts)
            }
        }

        processIterations(iterations, ruleCounts)
        return letterCounts.maxOf { it.value } - letterCounts.minOf { it.value }
    }

    val sampleInput = readInput("day14_sample_1")
    val input = readInput("day14_1")

    check(processPolymers(sampleInput.first(), getInsertionRules(sampleInput), 10) == 1588L)
    println("Part1: ${processPolymers(input.first(), getInsertionRules(input), 10)}")

    check(processPolymers(sampleInput.first(), getInsertionRules(sampleInput), 40) == 2188189693529)
    println("Part2: ${processPolymers(input.first(), getInsertionRules(input), 40)}")
}