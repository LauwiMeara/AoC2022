import kotlin.collections.ArrayDeque

fun main() {
    data class Procedure(val unit: Int, val startStack: Int, val destinationStack: Int)

    fun getCrates(input: String): Map<Int, ArrayDeque<Char>> {
        val splitInput = input.split(System.lineSeparator()).map{it.toCharArray()}
        val crates = mutableMapOf<Int, ArrayDeque<Char>>()

        // Get row index of all stack numbers
        var rowIndex = 0
        for (i in splitInput.indices) {
            if (splitInput[i].contains('1')) {
                rowIndex = i
                break
            }
        }

        var endReached = false
        var stackNumber = '1'
        while (!endReached) {
            // Get column index of a given stack number
            var columnIndex = 0
            for (i in splitInput[rowIndex].indices) {
                if (splitInput[rowIndex][i] == stackNumber) {
                    columnIndex = i
                    break
                }
            }
            // Add all crates from that index to stack if they are not null
            crates[stackNumber.digitToInt()] = ArrayDeque()
            for (line in splitInput) {
                if (line.size - 1 >= columnIndex &&
                    line[columnIndex] != ' ' &&
                    line[columnIndex] != stackNumber) {
                    crates[stackNumber.digitToInt()]?.add(line[columnIndex])
                }
            }

            stackNumber++
            if (splitInput.all{!it.contains(stackNumber)}) {
                endReached = true
            }
        }

        return crates
    }

    fun getProcedures(input: String): List<Procedure> {
        val splitInput = input.split(System.lineSeparator()).map{line -> line.split("move ", " from ", " to ").filter{ it.isNotEmpty() }}
        val procedures = mutableListOf<Procedure>()
        for (line in splitInput) {
            val unit = line[0].toInt()
            val startStack = line[1].toInt()
            val destinationStack = line[2].toInt()
            procedures.add(Procedure(unit, startStack, destinationStack))
        }
        return procedures
    }
    
    fun part1(input: List<String>): String {
        val crates = getCrates(input.first())
        val procedures = getProcedures(input.last())
        for (procedure in procedures) {
            for (i in 0 until procedure.unit) {
                val tempCrate = crates[procedure.startStack]?.removeFirst() ?: ' '
                crates[procedure.destinationStack]?.addFirst(tempCrate)
            }
        }
        var result = ""
        for (k in crates.keys) {
            result += crates[k]?.first()
        }
        return result
    }

    fun part2(input: List<String>): String {
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
        var result = ""
        for (k in crates.keys) {
            result += crates[k]?.first()
        }
        return result
    }

    val input = readInputSplitByDelimiter("Day05", "${System.lineSeparator()}${System.lineSeparator()}")

    println(part1(input))
    println(part2(input))
}
