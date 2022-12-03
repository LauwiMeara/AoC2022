const val START_ASCII_UPPERCASE = 'A'.code
const val START_ASCII_LOWERCASE = 'a'.code
const val START_ITEM_UPPERCASE = 27
const val START_ITEM_LOWERCASE = 1
const val GROUP_SIZE = 3

fun main() {
    fun calculatePriority(item: Char) : Int{
        return if (item.isUpperCase()) {
            item.code - START_ASCII_UPPERCASE + START_ITEM_UPPERCASE
        } else {
            item.code - START_ASCII_LOWERCASE + START_ITEM_LOWERCASE
        }
    }

    fun calculateSum(groups: List<List<String>>): Int {
        var sum = 0
        for (group in groups) {
            for (item in group.first()) {
                if (group.all{it.contains(item)}) {
                    sum += calculatePriority(item)
                    break
                }
            }
        }
        return sum
    }

    fun part1(input: List<String>): Int {
        val rucksacks = input.map{it.chunked(it.length / 2)}
        return calculateSum(rucksacks)
    }

    fun part2(input: List<String>): Int {
        val elfGroups = input.windowed(GROUP_SIZE,GROUP_SIZE)
        return calculateSum(elfGroups)
    }

    val input = readInputAsStrings("Day03")

    println(part1(input))
    println(part2(input))
}
