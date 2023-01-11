import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

const val CAVE_WIDTH = 7
const val HEIGHT_OFFSET_FALLING_ROCK = 3
const val LEFT_OFFSET_FALLING_ROCK = 2
const val CHAR_FALLING_ROCK = '@'
const val CHAR_LANDED_ROCK = '#'
const val NUM_ROCKS_PART_1 = 2022L
const val NUM_ROCKS_PART_2 = 1000000000000
const val FRAME_HEIGHT_DAY_17 = 1000
const val POINT_SIZE_DAY_17 = 30
const val INTERVAL_DAY_17 = 80L

data class Rock(val str: String, val size: Int, val width: Int = size)
data class State(val rockIndex: Long, val windIndex: Int, val heightPerColumn: List<Int>)

class TetrisPanel(var grid: Array<CharArray>) : JPanel() {
    init {
        this.background = Color.decode(AOC_BACKGROUND_COLOR)
    }

    private var frameHeight = FRAME_HEIGHT_DAY_17

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        var yDifference = frameHeight / POINT_SIZE_DAY_17 - grid.size - 1
        if (yDifference < 0) {
            frameHeight += 100
            yDifference = frameHeight / POINT_SIZE_DAY_17 - grid.size - 1
        }

        for (y in grid.indices) {
            for (x in grid[y].indices) {
                when (grid[y][x]) {
                    CHAR_LANDED_ROCK -> {
                        g.color = Color.DARK_GRAY
                        g.fillRect(x * POINT_SIZE_DAY_17, (y + yDifference) * POINT_SIZE_DAY_17, POINT_SIZE_DAY_17, POINT_SIZE_DAY_17)
                    }
                    CHAR_FALLING_ROCK -> {
                        g.color = Color.decode(AOC_COLOR_LIGHT_GREEN)
                        g.fillRect(x * POINT_SIZE_DAY_17, (y + yDifference) * POINT_SIZE_DAY_17, POINT_SIZE_DAY_17, POINT_SIZE_DAY_17)
                    }
                    else -> continue
                }
            }
        }
    }
}

fun main() {
    fun createFrame(panel: JPanel) {
        val width = CAVE_WIDTH * POINT_SIZE_DAY_17 + 15
        val height = FRAME_HEIGHT_DAY_17
        val frame = JFrame("Advent of Code, Day 17: Pyroclastic Flow")
        frame.setSize(width, height)
        frame.isVisible = true
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.add(panel)
        frame.validate()
    }

    fun getCaveWithFloor(): Array<CharArray> {
        return Array(1) { CharArray(CAVE_WIDTH) { CHAR_LANDED_ROCK } }
    }

    fun resizeCave(cave: Array<CharArray>, rockSize: Int): Array<CharArray> {
        val filteredCave = cave.filter { it.contains(CHAR_LANDED_ROCK) }
        val heightDifference = HEIGHT_OFFSET_FALLING_ROCK + rockSize
        val newCave = Array(filteredCave.size + heightDifference) { CharArray(CAVE_WIDTH) }
        for (y in filteredCave.indices) {
            for (x in 0 until CAVE_WIDTH) {
                newCave[y + heightDifference][x] = filteredCave[y][x]
            }
        }
        return newCave
    }

    fun addFallingRock(cave: Array<CharArray>, rock: Rock): Array<CharArray> {
        val newCave = resizeCave(cave, rock.size)
        val rockArr = rock.str.chunked(rock.size)
        for (y in rockArr.indices) {
            for (x in 0 until rock.size) {
                val char = rockArr[y][x]
                if (char == CHAR_FALLING_ROCK) {
                    newCave[y][x + LEFT_OFFSET_FALLING_ROCK] = char
                }
            }
        }
        return newCave
    }

    fun tryMovingRock(cave: Array<CharArray>, xMove: Int): Pair<Array<CharArray>, Boolean> {
        val newCave = Array(cave.size) { cave[it].clone() }
        for (y in newCave.indices) {
            for (x in if (xMove < 0) (0 until CAVE_WIDTH).drop(1) else (0 until CAVE_WIDTH).reversed().drop(1)) {
                if (newCave[y][x] == CHAR_FALLING_ROCK) {
                    if (newCave[y][x + xMove] == CHAR_LANDED_ROCK) {
                        return Pair(cave, false)
                    } else {
                        newCave[y][x + xMove] = CHAR_FALLING_ROCK
                        newCave[y][x] = Char.MIN_VALUE
                    }
                }
            }
        }
        return Pair(newCave, true)
    }

    fun tryFallingRock(cave: Array<CharArray>): Pair<Array<CharArray>, Boolean> {
        val newCave = Array(cave.size) { cave[it].clone() }
        for (y in newCave.indices.reversed().drop(1)) {
            for (x in 0 until CAVE_WIDTH) {
                if (newCave[y][x] == CHAR_FALLING_ROCK) {
                    if (newCave[y + 1][x] == CHAR_LANDED_ROCK) {
                        return Pair(cave, false)
                    } else {
                        newCave[y][x] = Char.MIN_VALUE
                        newCave[y + 1][x] = CHAR_FALLING_ROCK
                    }
                }
            }
        }
        return Pair(newCave, true)
    }

    fun landRock(cave: Array<CharArray>): Array<CharArray> {
        return cave.map{y -> y.map{if (it == CHAR_FALLING_ROCK) CHAR_LANDED_ROCK else it}.toCharArray()}.toTypedArray()
    }

    fun simulateFallingRocks(grid: Array<CharArray>, numRocks: Long, panel: TetrisPanel?, input: String, isPart2: Boolean = false): Long {
        val states = mutableListOf<Pair<State, Long>>()
        var foundCycle = false
        var height = 0L
        var cave = grid
        var windIndex = 0
        var rockIndex = 1L
        while (rockIndex <= numRocks) {
            // Let rock appear.
            val rock = when (rockIndex % 5) {
                1L -> Rock("............@@@@", 4)
                2L -> Rock(".@.@@@.@.", 3)
                3L -> Rock("..@..@@@@", 3)
                4L -> Rock("@...@...@...@...", 4, 1)
                else -> Rock("@@@@", 2, 2)
            }
            cave = addFallingRock(cave, rock)
            if (panel != null) {
                Thread.sleep(INTERVAL_DAY_17)
                panel.grid = cave.copyOf()
                panel.repaint()
            }

            // Let rock move and fall.
            var isFalling = true
            var xRock = LEFT_OFFSET_FALLING_ROCK
            var yRock = cave.indexOfFirst { it.contains(CHAR_FALLING_ROCK) }
            while (isFalling) {
                // Move with the wind (as long as there isn't a wall or other rock).
                val wind = input[windIndex % input.length]
                if (wind == '<') {
                    // Move left.
                    if (xRock - 1 >= 0) {
                        val (newCave, isMoved) = tryMovingRock(cave, -1)
                        if (isMoved) {
                            cave = newCave
                            xRock--
                        }
                    }
                } else {
                    // Move right.
                    if (xRock + rock.width < CAVE_WIDTH) {
                        val (newCave, hasMoved) = tryMovingRock(cave, 1)
                        if (hasMoved) {
                            cave = newCave
                            xRock++
                        }
                    }
                }
                if (panel != null) {
                    Thread.sleep(INTERVAL_DAY_17)
                    panel.grid = cave.copyOf()
                    panel.repaint()
                }

                // Fall down.
                if (yRock + 1 < cave.size) {
                    val (newCave, hasFallen) = tryFallingRock(cave)
                    if (hasFallen) {
                        cave = newCave
                        yRock++
                    } else {
                        isFalling = false
                        cave = landRock(cave)
                        if (isPart2 && !foundCycle) {
                            val state = State(rockIndex % 5, windIndex % input.length, cave.map{it.size - 1})
                            val indexInStates = states.indexOfFirst{it.first == state}
                            if (indexInStates != -1) {
                                // Found a cycle.
                                foundCycle = true
                                val numRocksPerCycle = states.size - indexInStates
                                val heightPerCycle = height - states[indexInStates].second
                                val numRocksLeft = NUM_ROCKS_PART_2 - rockIndex
                                val numCycles = numRocksLeft / numRocksPerCycle
                                height += (numCycles * heightPerCycle)
                                rockIndex = numRocks - (numRocksLeft % numRocksPerCycle)
                            }
                            states.add(Pair(state, height))
                        }
                        rockIndex++
                        val yFull = cave.indexOfFirst{y -> y.all{it == CHAR_LANDED_ROCK}}
                        if (yFull != -1 && yFull != cave.size - 1) {
                            height += (cave.size - 1 - yFull)
                            val newCave = Array(yFull + 1) { CharArray(CAVE_WIDTH) }
                            for (y in newCave.indices) {
                                for (x in 0 until CAVE_WIDTH) {
                                    newCave[y][x] = cave[y][x]
                                }
                            }
                            cave = newCave
                        }
                    }
                }
                windIndex++
                if (panel != null) {
                    Thread.sleep(INTERVAL_DAY_17)
                    panel.grid = cave.copyOf()
                    }
                }
                if (visualise) {
                    Thread.sleep(INTERVAL_DAY_17)
                    panel!!.grid = cave.copyOf()
                    panel.repaint()
                }
            }
        }
        height += cave.filter{it.contains(CHAR_LANDED_ROCK)}.size.toLong() - 1
        return height
    }

    fun part1(input: String, visualise: Boolean = false): Long {
        val cave = getCaveWithFloor()
        val panel = if (visualise) TetrisPanel(cave) else null
        if (panel != null) createFrame(panel)
        return simulateFallingRocks(cave, NUM_ROCKS_PART_1, panel, input)
    }

    fun part2(input: String, visualise: Boolean = false): Long {
        val cave = getCaveWithFloor()
        val panel = if (visualise) TetrisPanel(cave) else null
        if (visualise) createFrame(panel!!)
        return simulateFallingRocks(cave, NUM_ROCKS_PART_2, panel, input, true)
    }

    val input = readInput("Day17")

    println(part1(input))
    part1(input, true)
    println(part2(input))
}
