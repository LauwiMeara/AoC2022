const val RELIEF = 3
const val ROUNDS_PART_1 = 20
const val ROUNDS_PART_2 = 10000

data class Monkey (val items: MutableList<Long>, val operation: List<String>, val testDivisibleBy: Int, val indexIfTestTrue: Int, val indexIfTestFalse: Int, var totalNumOfInspectedItems: Int)

fun main() {
    fun getMonkeys(input: List<String>): List<Monkey> {
        val monkeys = mutableListOf<Monkey>()
        for (line in input) {
            val monkey = line.split(System.lineSeparator())
            val items = monkey[1].split(": ",", ").drop(1).map{it.toLong()}.toMutableList()
            val operation = monkey[2].substringAfter("new = old ").split(" ")
            val testDivisibleBy = monkey[3].substringAfter("divisible by ").toInt()
            val indexIfTestTrue = monkey[4].substringAfter("monkey ").toInt()
            val indexIfTestFalse = monkey[5].substringAfter("monkey ").toInt()
            monkeys.add(Monkey(items, operation, testDivisibleBy, indexIfTestTrue, indexIfTestFalse, 0))
        }
        return monkeys
    }

    fun part1(input: List<String>): Int {
        val monkeys = getMonkeys(input)
        for (round in 1..ROUNDS_PART_1) {
            for (monkey in monkeys) {
                for (i in monkey.items.indices) {
                    var item = monkey.items[i]
                    // Monkey plays with the item. Worry intensifies.
                    val worry = if (monkey.operation.last() == "old") item else monkey.operation.last().toLong()
                    when (monkey.operation.first()) {
                        "*" -> item *= worry
                        else -> item += worry
                    }
                    // Monkey gets bored with the item. Worry calms down.
                    item /= RELIEF
                    // Monkey throws the item to a different monkey.
                    if (item.toInt() % monkey.testDivisibleBy == 0) {
                        monkeys[monkey.indexIfTestTrue].items.add(item)
                    } else {
                        monkeys[monkey.indexIfTestFalse].items.add(item)
                    }
                    monkey.totalNumOfInspectedItems++
                }
                monkey.items.clear()
            }
        }
        return monkeys.map{it.totalNumOfInspectedItems}.sortedDescending().subList(0, 2).reduce{acc, it -> acc * it}
    }

    fun part2(input: List<String>): Long {
        val monkeys = getMonkeys(input)
        val totalTestDivisibleBy = monkeys.map{it.testDivisibleBy}.reduce{acc, it -> acc * it}
        for (round in 1..ROUNDS_PART_2) {
            for (monkey in monkeys) {
                for (i in monkey.items.indices) {
                    var item = monkey.items[i]
                    // Monkey plays with the item. Worry intensifies.
                    val worry = if (monkey.operation.last() == "old") item else monkey.operation.last().toLong()
                    when (monkey.operation.first()) {
                        "*" -> item *= worry
                        else -> item += worry
                    }
                    // Keep worry level manageable by dividing by the totalTestDivisibleBy.
                    item %= totalTestDivisibleBy
                    // Monkey throws the item to a different monkey.
                    if (item % monkey.testDivisibleBy == 0L) {
                        monkeys[monkey.indexIfTestTrue].items.add(item)
                    } else {
                        monkeys[monkey.indexIfTestFalse].items.add(item)
                    }
                    monkey.totalNumOfInspectedItems++
                }
                monkey.items.clear()
            }
        }
        return monkeys.map{it.totalNumOfInspectedItems.toLong()}.sortedDescending().subList(0, 2).reduce{acc, it -> acc * it}
    }

    val input = readInputSplitByDelimiter("Day11", "${System.lineSeparator()}${System.lineSeparator()}")

    println(part1(input))
    println(part2(input))
}