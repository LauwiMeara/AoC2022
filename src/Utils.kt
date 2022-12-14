import java.io.File

const val NUM_THREADS = 4
const val AOC_BACKGROUND_COLOR = "#0f0f23"
const val AOC_COLOR_LIGHT_GREEN = "#00cc00"

data class Point(val x: Int, val y: Int)

enum class Direction {
    RIGHT, LEFT, UP, DOWN
}

/**
 * Reads from the given input txt file as one string.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readText()

/**
 * Reads from the given input txt file as strings split by the given delimiter.
 */
fun readInputSplitByDelimiter(name: String, delimiter: String) = File("src", "$name.txt")
    .readText()
    .split(delimiter)

/**
 * Reads lines from the given input txt file as strings.
 */
fun readInputAsStrings(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Reads lines from the given input txt file as integers.
 */
fun readInputAsInts(name: String) = File("src", "$name.txt")
    .readLines()
    .map{it.toInt()}
