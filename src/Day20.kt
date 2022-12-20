import kotlin.math.abs

const val DECRYPTION_KEY = 811589153

fun main() {
    fun createSequence(input: List<Int>, isPart2: Boolean = false): MutableList<Pair<Int, Long>> {
        val sequence = mutableListOf<Pair<Int, Long>>()
        for (i in input.indices) {
            if (isPart2) {
                sequence.add(Pair(i, input[i].toLong() * DECRYPTION_KEY))
            } else {
                sequence.add(Pair(i, input[i].toLong()))
            }
        }
        return sequence
    }

    fun moveNumbers(sequence: MutableList<Pair<Int, Long>>): MutableList<Pair<Int, Long>> {
        val sequenceSize = sequence.size
        for (i in sequence.indices) {
            val currentIndex = sequence.indexOfFirst{it.first == i}
            val numberPair = sequence.removeAt(currentIndex)
            var newIndex = currentIndex + numberPair.second
            if (newIndex < 0) {
                newIndex = (-(abs(newIndex) % (sequenceSize - 1)) + sequenceSize - 1) % (sequenceSize - 1)
            } else if (newIndex >= sequenceSize) {
                newIndex %= (sequenceSize - 1)
            }
            sequence.add(newIndex.toInt(), numberPair)
        }
        return sequence
    }

    fun getResult(sequence: List<Pair<Int, Long>>): Long {
        val index0 = sequence.indexOfFirst{it.second == 0L}
        val n1000 = sequence[(index0 + 1000) % sequence.size].second
        val n2000 = sequence[(index0 + 2000) % sequence.size].second
        val n3000 = sequence[(index0 + 3000) % sequence.size].second
        return n1000 + n2000 + n3000
    }

    fun part1(input: List<Int>): Long {
        var sequence = createSequence(input)
        sequence = moveNumbers(sequence)
        return getResult(sequence)
    }

    fun part2(input: List<Int>): Long {
        var sequence = createSequence(input, true)
        for (round in 0 until 10) {
            sequence = moveNumbers(sequence)
        }
        return getResult(sequence)
    }

    val input = readInputAsInts("Day20")

    println(part1(input))
    println(part2(input))
}
