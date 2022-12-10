const val MAX_CYCLE = 220
const val FIRST_IMPORTANT_CYCLE = 20
const val ITERATION_IMPORTANT_CYCLES = 40
const val SCREEN_HEIGHT = 6
const val SCREEN_WIDTH = 40

fun main() {
    fun nextCyclePart1(cycle: Int, x: Int, signalStrengthPerCycle: MutableMap<Int, Int>): Int {
        val importantCycle = (cycle - FIRST_IMPORTANT_CYCLE) % ITERATION_IMPORTANT_CYCLES == 0
        if (importantCycle) {
            signalStrengthPerCycle[cycle] = cycle * x
        }
        return cycle + 1
    }

    fun nextCyclePart2(crtPosition: Int, x: Int, screen: Array<CharArray>): Int {
        val col = crtPosition % SCREEN_WIDTH
        val pixelIsActive = col == x - 1 || col == x || col == x + 1 // x is the col index of the middle of three horizontally aligned active pixels
        if (pixelIsActive) {
            screen[crtPosition / SCREEN_WIDTH][col] = '#'
        }
        return crtPosition + 1
    }

    fun getScreen(): Array<CharArray> {
        val screen = Array(SCREEN_HEIGHT) { CharArray(SCREEN_WIDTH) }
        for (i in 0 until SCREEN_HEIGHT) {
            for (j in 0 until SCREEN_WIDTH) {
                screen[i][j] = '.'
            }
        }
        return screen
    }

    fun printScreen(screen: Array<CharArray>) {
        for (row in screen) {
            println(row.joinToString(""))
        }
    }

    fun part1(input: List<String>): Int {
        val signalStrengthPerCycle = mutableMapOf<Int, Int>()
        var x = 1
        var cycle = 1
        while (cycle <= MAX_CYCLE) {
            for (instruction in input) {
                cycle = nextCyclePart1(cycle, x, signalStrengthPerCycle)
                if (instruction.startsWith("addx")) {
                    cycle = nextCyclePart1(cycle, x, signalStrengthPerCycle)
                    x += instruction.split(" ").last().toInt()
                }
            }
        }
        return signalStrengthPerCycle.values.sum()
    }

    fun part2(input: List<String>) {
        val screen = getScreen()
        var x = 1 // x is the col index of the middle of three horizontally aligned active pixels
        var crtPosition = 0
        for (instruction in input) {
            crtPosition = nextCyclePart2(crtPosition, x, screen)
            if (instruction.startsWith("addx")) {
                crtPosition = nextCyclePart2(crtPosition, x, screen)
                x += instruction.split(" ").last().toInt()
            }
        }
        printScreen(screen)
    }

    val input = readInputAsStrings("Day10")

    println(part1(input))
    part2(input)
}
