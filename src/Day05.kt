import kotlin.collections.ArrayDeque

const val FIRST_STACK_NUMBER = '1'

fun main() {
    data class Procedure(val unit: Int, val startStack: Int, val destinationStack: Int)

    fun getCrates(input: List<String>): Map<Int, ArrayDeque<Char>> {
        val crates = mutableMapOf<Int, ArrayDeque<Char>>()

        // Get rowIndex of all stackNumbers.
        val rowIndex = input.indexOfFirst{it.contains(FIRST_STACK_NUMBER)}

        var stackNumber = '1'
        var endReached = false
        while (!endReached) {
            // Get columnIndex of the given stackNumber.
            val columnIndex = input[rowIndex].indexOfFirst{it == stackNumber}

            // Fill the stack of the specified stackNumber with all crates on the same columnIndex.
            crates[stackNumber.digitToInt()] = ArrayDeque()
            for (line in input) {
                if (line.length - 1 >= columnIndex &&
                    line[columnIndex] != ' ' &&
                    line[columnIndex] != stackNumber) {
                    crates[stackNumber.digitToInt()]?.add(line[columnIndex])
                }
            }

            // Up the stackNumber and check if this stackNumber still exists. If not, end the loop.
            stackNumber++
            if (input.all{!it.contains(stackNumber)}) {
                endReached = true
            }
        }

        return crates
    }

    fun getProcedures(input: List<String>): List<Procedure> {
        val splitInput = input.map{line -> line.split("move ", " from ", " to ").filter{ it.isNotEmpty() }}
        val procedures = mutableListOf<Procedure>()
        for (line in splitInput) {
            val unit = line[0].toInt()
            val startStack = line[1].toInt()
            val destinationStack = line[2].toInt()
            procedures.add(Procedure(unit, startStack, destinationStack))
        }
        return procedures
    }

    fun part1(input: List<List<String>>): String {
        val crates = getCrates(input.first())
        val procedures = getProcedures(input.last())
        for (procedure in procedures) {
            for (i in 0 until procedure.unit) {
                val tempCrate = crates[procedure.startStack]?.removeFirst() ?: ' '
                crates[procedure.destinationStack]?.addFirst(tempCrate)
            }
        }
        return crates.values.map{it.first()}.joinToString("")
    }

    fun part2(input: List<List<String>>): String {
        val crates = getCrates(input.first())
        val procedures = getProcedures(input.last())
        for (procedure in procedures) {
            val tempCrates = mutableListOf<Char>()
            for (i in 0 until procedure.unit) {
                tempCrates.add(crates[procedure.startStack]?.removeFirst() ?: ' ')
            }
            for (i in tempCrates.size - 1 downTo 0) {
                crates[procedure.destinationStack]?.addFirst(tempCrates[i])
            }
        }
        return crates.values.map{it.first()}.joinToString("")
    }

    val input = readInputSplitByDelimiter("Day05", "${System.lineSeparator()}${System.lineSeparator()}")
        .map{it.split(System.lineSeparator())}

    println(part1(input))
    println(part2(input))
}
