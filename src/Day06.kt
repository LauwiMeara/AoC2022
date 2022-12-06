const val SIZE_PART_1 = 4
const val SIZE_PART_2 = 14

fun main() {
    fun getDistinctIndex(windowedInput: List<String>, size: Int): Int {
        for (i in windowedInput.indices) {
            if (windowedInput[i].toList().distinct().size == size) {
                return i
            }
        }
        return -1
    }

    fun part1(input: String): Int {
        val windowedInput = input.windowed(SIZE_PART_1, 1)
        return getDistinctIndex(windowedInput, SIZE_PART_1) + SIZE_PART_1
    }

    fun part2(input: String): Int {
        val windowedInput = input.windowed(14, 1)
        return getDistinctIndex(windowedInput, SIZE_PART_2) + SIZE_PART_2
    }

    val input = readInputAsStrings("Day06").joinToString("")

    println(part1(input))
    println(part2(input))
}
