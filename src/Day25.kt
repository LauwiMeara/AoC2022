import kotlin.math.pow

fun main() {
    fun getStringLength(decimalSum: Long): Int {
        var i = 0
        while (true) {
            val place = 5.toDouble().pow(i).toLong()
            if (place <= decimalSum) {
                i++
            } else {
                break
            }
        }
        return i
    }

    fun part1(input: List<String>): String {
        // Get the sum of the input in decimals.
        val decimalInput = mutableListOf<Long>()
        for (line in input) {
            val decimalLine = mutableListOf<Long>()
            for (i in line.indices) {
                val decimalChar = when (line[i]) {
                    '=' -> -2 * 5.toDouble().pow((line.length - 1 - i)).toLong()
                    '-' -> -1 * 5.toDouble().pow((line.length - 1 - i)).toLong()
                    else -> line[i].digitToInt() * 5.toDouble().pow((line.length - 1 - i)).toLong()
                }
                decimalLine.add(decimalChar)
            }
            decimalInput.add(decimalLine.sum())
        }
        var decimalSum = decimalInput.sum()
        println("DecimalSum: $decimalSum")

        // Transform decimalSum to SNAFU.
        val snafu = mutableListOf<String>()
        val snafuLength = getStringLength(decimalSum)
        for (i in snafuLength - 1 downTo 0) {
            if (decimalSum - (5.toDouble().pow(i).toLong() * 1) < 0) {
                snafu.add("0")
            } else for (j in 4 downTo 0) {
                if (decimalSum - (5.toDouble().pow(i).toLong() * j) >= 0) {
                    snafu.add(j.toString())
                    decimalSum -= (5.toDouble().pow(i).toLong() * j)
                    break
                }
            }
        }
        println("Incorrect SNAFU: ${snafu.joinToString("")}")
        // Transform 3's, 4's and 5's to ='s, -'s and 0's respectively.
        for (i in snafu.size - 1 downTo 0) {
            when (snafu[i]) {
                "3" -> {
                    snafu[i - 1] = (snafu[i - 1].toInt() + 1).toString()
                    snafu[i] = "="
                }
                "4" -> {
                    snafu[i - 1] = (snafu[i - 1].toInt() + 1).toString()
                    snafu[i] = "-"
                }
                "5" -> {
                    snafu[i - 1] = (snafu[i - 1].toInt() + 1).toString()
                    snafu[i] = "0"
                }
            }
        }
        print("SNAFU: ")

        return snafu.joinToString("")
    }

    val input = readInputAsStrings("Day25")

    println(part1(input))
}
