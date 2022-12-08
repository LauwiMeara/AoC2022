fun main() {
    fun calculateViewingDistance(range: IntProgression, isRowIndex: Boolean, input: List<List<Int>>, row: Int, col: Int): Int {
        var viewingDistance = 0
        for (i in range) {
            viewingDistance++
            if (isRowIndex && input[i][col] >= input[row][col] ||
                !isRowIndex && input[row][i] >= input[row][col]) {
                break
            }
        }
        return viewingDistance
    }

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
                val up = calculateViewingDistance((row - 1 downTo 0), true, input, row, col)
                val down = calculateViewingDistance((row + 1 until input.size), true, input, row, col)
                val left = calculateViewingDistance((col - 1 downTo 0), false, input, row, col)
                val right = calculateViewingDistance((col + 1 until input.first().size), false, input, row, col)
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
