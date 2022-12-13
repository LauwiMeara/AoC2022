const val OPEN_BRACKET = '['
const val CLOSE_BRACKET = ']'
const val COMMA = ','
const val DIVIDER_PACKET_1 = "[[2]]"
const val DIVIDER_PACKET_2 = "[[6]]"

data class PacketPair (var left: String, var right: String)

fun main() {
    fun addBrackets(partPair: String, iChar: Int): String {
        val prev = partPair.substring(0, iChar)
        val nextTotal = partPair.substring(iChar, partPair.length)
        val indexAfterNumber = listOf(nextTotal.indexOf(CLOSE_BRACKET), nextTotal.indexOf(COMMA)).filter{it >= 0}.min()
        val next1 = nextTotal.substring(0, indexAfterNumber)
        val next2 = nextTotal.substring(indexAfterNumber, nextTotal.length)
        return "$prev[$next1]$next2"
    }

    fun getNumber(partPair: String, iChar: Int): Int {
        val next = partPair.substring(iChar, partPair.length)
        val indexAfterNumber = listOf(next.indexOf(CLOSE_BRACKET), next.indexOf(COMMA)).filter{it >= 0}.min()
        return next.substring(0, indexAfterNumber).toInt()
    }

    fun pairIsCorrectlyOrdered(pair: PacketPair, printProgress: Boolean): Boolean {
        var isCorrectOrder = true
        for (iChar in 0 until pair.left.length) {
            val leftChar = pair.left[iChar]
            val rightChar = pair.right[iChar]
            
            if (printProgress) {
                println("Left: $leftChar}   Right: $rightChar")
            }

            // If both chars are the same and are not numbers, check the next chars.
            if (leftChar == rightChar && leftChar.digitToIntOrNull() == null) continue

            // If leftChar is CLOSE_BRACKET (and rightChar isn't), the order is correct.
            else if (leftChar == CLOSE_BRACKET) break
            // If rightChar is CLOSE_BRACKET (and leftChar isn't), the order is incorrect.
            else if (rightChar == CLOSE_BRACKET) {
                isCorrectOrder = false
                break
            }

            // If one char is OPEN_BRACKET, add brackets to the other char and check the next chars.
            else if (leftChar == OPEN_BRACKET) {
                pair.right = addBrackets(pair.right, iChar)
                continue
            } else if (rightChar == OPEN_BRACKET) {
                pair.left = addBrackets(pair.left, iChar)
                continue
            }

            // If we reach this point, the chars are numbers.
            // If the left number is smaller, the order is correct; if the left number is bigger, the order is incorrect.
            val leftNumber = getNumber(pair.left, iChar)
            val rightNumber = getNumber(pair.right, iChar)
            if (leftNumber < rightNumber) break
            else if (leftNumber > rightNumber) {
                isCorrectOrder = false
                break
            }
        }

        if (printProgress) {
            val result = if (isCorrectOrder) "CORRECT" else "INCORRECT"
            println(result)
        }

        return isCorrectOrder
    }

    fun part1(input: List<PacketPair>, printProgress: Boolean = true): Int {
        val indicesOfCorrectPairs = mutableListOf<Int>()
        for (iPair in input.indices) {
            val pair = input[iPair]
            if (printProgress) {
                println("Pair: ${iPair + 1}")
                println(pair.left)
                println(pair.right)
            }

            // If pair is in correct order, add index + 1 to the list.
            if (pairIsCorrectlyOrdered(pair, printProgress)) {
                indicesOfCorrectPairs.add(iPair + 1)
            }
        }
        return indicesOfCorrectPairs.sum()
    }

    fun part2(input: List<String>, printProgress: Boolean = true): Int {
        val dividerPackets = listOf(DIVIDER_PACKET_1, DIVIDER_PACKET_2)
        val fullInput = (input + dividerPackets).toMutableList()

        // Sort input using Bubble Sort.
        var isCorrectOrder = false
        while (!isCorrectOrder) {
            for (i in 0 until fullInput.size - 1) {
                if (printProgress) {
                    println("Checking packet ${i + 1} and ${i + 2}")
                }
                val pair = PacketPair(fullInput[i], fullInput[i + 1])
                if (!pairIsCorrectlyOrdered(pair, false)) {
                    val temp = fullInput[i]
                    fullInput[i] = fullInput[i + 1]
                    fullInput[i + 1] = temp
                    break
                } else if (i == fullInput.size - 2) {
                    isCorrectOrder = true
                    break
                }
            }
        }

        // Find indexes of the divider packets.
        val index1 = fullInput.indexOf(DIVIDER_PACKET_1) + 1
        val index2 = fullInput.indexOf(DIVIDER_PACKET_2) + 1
        return index1 * index2
    }

    val inputPart1 = readInputSplitByDelimiter("Day13", "${System.lineSeparator()}${System.lineSeparator()}")
        .map{it.split(System.lineSeparator())}
        .map{PacketPair(it.first(), it.last())}
    val inputPart2 = readInputAsStrings("Day13").filter{it.isNotEmpty()}

    println(part1(inputPart1, false))
    println(part2(inputPart2, false))
}
