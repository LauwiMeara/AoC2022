fun main() {
    fun part1(input: List<List<Int>>): Int {
        return input.maxOf { it.sum() }
    }

    fun part2(input: List<List<Int>>): Int {
        return input.map{it.sum()}.sortedDescending().subList(0, 3).sum()
    }

    val input = readInputSplitByDelimiter("Day01","${System.lineSeparator()}${System.lineSeparator()}")
        .map{it.split(System.lineSeparator())
            .map{calories -> calories.toInt()}}

    println(part1(input))
    println(part2(input))
}
