fun main() {
    fun part1(input: List<List<Int>>): Int {
        var numOfVisibleTrees = 0
        for (row in input.indices) {
            for (col in input.first().indices) {
                // Trees on the outside border are always visible.
                if (row == 0 || row == input.size - 1 ||
                    col == 0 || col == input.first().size - 1) {
                    numOfVisibleTrees++
                } else {
                    // Trees within are visible if any trees up, down, left or right are higher.
                    if (input.subList(0, row).isNotEmpty() && input.subList(0, row).all{it[col] < input[row][col]} ||
                        input.subList(row + 1, input.size).isNotEmpty() && input.subList(row + 1, input.size).all{it[col] < input[row][col]} ||
                        input[row].subList(0, col).isNotEmpty() && input[row].subList(0, col).all{it < input[row][col]} ||
                        input[row].subList(col + 1, input[row].size).isNotEmpty() && input[row].subList(col + 1, input[row].size).all{it < input[row][col]}) {
                        numOfVisibleTrees++
                    }
                }
            }
        }
        return numOfVisibleTrees
    }

    fun part2(input: List<List<Int>>): Int {
        val scenicScores = mutableListOf<Int>()
        for (row in input.indices) {
            for (col in input.first().indices) {
                // Calculate viewing distance upwards.
                var up = 0
                for (i in row - 1 downTo 0) {
                    up++
                    if (input[i][col] >= input[row][col]) {
                        break
                    }
                }
                // Calculate viewing distance downwards.
                var down = 0
                for (i in row + 1 until input.size) {
                    down++
                    if (input[i][col] >= input[row][col]) {
                        break
                    }
                }
                // Calculate viewing distance to the left.
                var left = 0
                for (i in col - 1 downTo 0) {
                    left++
                    if (input[row][i] >= input[row][col]) {
                        break
                    }
                }
                // Calculate viewing distance to the right.
                var right = 0
                for (i in col + 1 until input.first().size) {
                    right++
                    if (input[row][i] >= input[row][col]) {
                        break
                    }
                }
                scenicScores.add(up * down * left * right)
            }
        }
        return scenicScores.max()
    }

    val input = readInputAsStrings("Day08")
        .map{line -> line.toCharArray()
            .map{ it.digitToInt() }}

    println(part1(input))
    println(part2(input))
}
