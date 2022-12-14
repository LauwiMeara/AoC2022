import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

const val ROCK = '#'
const val SAND = 'o'
const val POINT_SIZE_DAY_14 = 2
const val INTERVAL_DAY_14 = 1

class CavePanel(var cave: Array<CharArray>, var positionFallingSand: Point) : JPanel() {
    init {
        this.background = Color.decode(AOC_BACKGROUND_COLOR)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // Color all units in the cave.
        for (x in cave.indices) {
            for (y in cave[x].indices) {
                when (cave[x][y]) {
                    ROCK -> {
                        g.color = Color.LIGHT_GRAY
                        g.fillRect(y * POINT_SIZE_DAY_14, x * POINT_SIZE_DAY_14, POINT_SIZE_DAY_14, POINT_SIZE_DAY_14)
                    }
                    SAND -> {
                        g.color = Color.ORANGE
                        g.fillRect(y * POINT_SIZE_DAY_14, x * POINT_SIZE_DAY_14, POINT_SIZE_DAY_14, POINT_SIZE_DAY_14)
                    }
                    else -> continue
                }
            }
        }

        // Color falling unit.
        g.fillRect( positionFallingSand.y * POINT_SIZE_DAY_14, positionFallingSand.x * POINT_SIZE_DAY_14, POINT_SIZE_DAY_14, POINT_SIZE_DAY_14)
    }
}

fun main() {
    fun createFrame(panel: JPanel, cave: Array<CharArray>) {
        val width = cave.first().size * POINT_SIZE_DAY_14 + 50
        val height = cave.size * POINT_SIZE_DAY_14 + 50
        val frame = JFrame("Advent of Code, Day 14: Regolith Reservoir")
        frame.setSize(width, height)
        frame.isVisible = true
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.add(panel)
        frame.validate()
    }

    fun initCave(input: List<List<Point>>, hasFloor: Boolean): Array<CharArray> {
        val maxX = if (hasFloor) input.flatten().maxOf { it.x } + 2 else input.flatten().maxOf { it.x }
        val maxY = if (hasFloor) input.flatten().maxOf { it.y } + maxX else input.flatten().maxOf { it.y }
        return Array(maxX + 1) { CharArray(maxY + 1) }
    }

    fun getCaveWithRockPaths(input:List<List<Point>>, hasFloor: Boolean = false): Array<CharArray> {
        val cave = initCave(input, hasFloor)
        for (rockPath in input) {
            for (i in 0 until rockPath.size - 1) {
                if (rockPath[i].x == rockPath[i + 1].x) {
                    // Horizontal line.
                    val x = rockPath[i].x
                    val minY = if (rockPath[i].y < rockPath[i + 1].y) rockPath[i].y else rockPath[i + 1].y
                    val maxY = if (rockPath[i].y > rockPath[i + 1].y) rockPath[i].y else rockPath[i + 1].y
                    for (y in minY..maxY) {
                        cave[x][y] = ROCK
                    }
                } else {
                    // Vertical line.
                    val y = rockPath[i].y
                    val minX = if (rockPath[i].x < rockPath[i + 1].x) rockPath[i].x else rockPath[i + 1].x
                    val maxX = if (rockPath[i].x > rockPath[i + 1].x) rockPath[i].x else rockPath[i + 1].x
                    for (x in minX..maxX) {
                        cave[x][y] = ROCK
                    }
                }
            }
        }
        if (hasFloor) {
            val x = cave.size - 1
            for (y in cave[x].indices) {
                cave[x][y] = ROCK
            }
        }
        return cave
    }

    fun simulateFallingSand(cave: Array<CharArray>, visualise: Boolean) {
        val startPosition = Point(0, 500)
        val panel = if (visualise) CavePanel(cave, startPosition) else null
        if (visualise) createFrame(panel!!, cave)
        var simulate = true
        while (simulate) {
            if (visualise) {
                Thread.sleep(INTERVAL_DAY_14.toLong())
                panel!!.cave = cave.copyOf()
                panel.repaint()
            }
            var x = startPosition.x
            var y = startPosition.y
            var isFalling = true
            while (isFalling) {
                if (visualise) {
                    Thread.sleep(INTERVAL_DAY_14.toLong())
                    panel!!.positionFallingSand = Point(x, y)
                    panel.repaint()
                }
                if (x + 1 >= cave.size) {
                    // Fall into endless void, stop simulation.
                    simulate = false
                    break
                } else {
                    if (cave[x + 1][y] == Char.MIN_VALUE) {
                        // Fall down.
                        x++
                    } else if (y - 1 >= cave[x + 1].size) {
                        // Fall into endless void, stop simulation.
                        simulate = false
                        break
                    } else if (cave[x + 1][y - 1] == Char.MIN_VALUE) {
                        // Fall down-left.
                        x++
                        y--
                    } else if (y + 1 >= cave[x + 1].size) {
                        // Fall into endless void, stop simulation.
                        simulate = false
                        break
                    } else if (cave[x + 1][y + 1] == Char.MIN_VALUE) {
                        // Fall down-right.
                        x++
                        y++
                    } else {
                        // Rest.
                        cave[x][y] = SAND
                        isFalling = false
                        // Source of falling sand is blocked, stop simulation.
                        if (x == startPosition.x && y == startPosition.y){
                            simulate = false
                        }
                    }
                }
            }
        }
    }

    fun part1(input: List<List<Point>>, visualise: Boolean = false): Int {
        val cave = getCaveWithRockPaths(input)
        simulateFallingSand(cave, visualise)
        return cave.flatMap{it.asIterable()}.count{it == SAND}
    }

    fun part2(input: List<List<Point>>, visualise: Boolean = false): Int {
        val cave = getCaveWithRockPaths(input, true)
        simulateFallingSand(cave, visualise)
        return cave.flatMap{it.asIterable()}.count{it == SAND}
    }

    val input = readInputAsStrings("Day14")
        .map{line -> line.split(" -> ")
            .map{it.split(",")}
            .map{(Point(it.last().toInt(), it.first().toInt()))}}

    println(part1(input))
    println(part2(input))
    part2(input, true)
}
