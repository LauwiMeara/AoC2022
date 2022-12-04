fun main() {
    fun part1(input: List<List<IntRange>>): Int {
        var count = 0
        for (pair in input) {
            val intersection = pair.first().intersect(pair.last())
            if (intersection == pair.first().toSet() || intersection == pair.last().toSet()) {
                count++
            }
        }
        return count
    }

    fun part2(input: List<List<IntRange>>): Int {
        var count = 0
        for (pair in input) {
            val intersection = pair.first().intersect(pair.last())
            if (intersection.isNotEmpty()) {
                count++
            }
        }
        return count
    }

    val input = readInputAsStrings("Day04")
        .map{pair -> pair.split(",")
            .map{elf -> elf.substringBefore("-").toInt()..elf.substringAfter("-").toInt()}}

    println(part1(input))
    println(part2(input))
}
