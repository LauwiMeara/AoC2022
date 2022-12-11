import java.awt.Color
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants.EXIT_ON_CLOSE
import kotlin.math.abs

const val NUM_OF_KNOTS_PART_2 = 9
const val FRAME_WIDTH = 548
const val FRAME_HEIGHT = 182
const val POINT_WIDTH = 3
const val POINT_HEIGHT = 3
const val OFFSET_WIDTH = 466
const val OFFSET_HEIGHT = 127
const val INTERVAL = 2

enum class Direction {
    RIGHT, LEFT, UP, DOWN
}

data class Motion(val direction: Direction, val unit: Int)
data class Point(val x: Int, val y: Int)

class KnotPanel(var headPositions: MutableList<Point>, var knotPositions: MutableList<MutableList<Point>>) : JPanel() {
    init {
        this.background = Color.decode("#0f0f23")
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        // Color all past positions of the tail knot.
        g.color = Color.DARK_GRAY
        if (knotPositions.isNotEmpty() && knotPositions.last().isNotEmpty()) {
            for (tail in knotPositions.last().subList(0, knotPositions.last().size - 1)) {
                g.fillRect((tail.x + OFFSET_WIDTH) * POINT_WIDTH, (tail.y + OFFSET_HEIGHT) * POINT_HEIGHT, POINT_WIDTH, POINT_HEIGHT)
            }
        }
        // Color the current position of the head and all knots.
        g.color = Color.decode("#00cc00")
        if (headPositions.size > 0) {
            g.fillRect((headPositions.last().x + OFFSET_WIDTH) * POINT_WIDTH, (headPositions.last().y + OFFSET_HEIGHT) * POINT_HEIGHT, POINT_WIDTH, POINT_HEIGHT)
            for (knotPositions in knotPositions) {
                g.fillRect((knotPositions.last().x + OFFSET_WIDTH) * POINT_WIDTH, (knotPositions.last().y + OFFSET_HEIGHT) * POINT_HEIGHT, POINT_WIDTH, POINT_HEIGHT)
            }
        }
    }

}

fun main() {
    fun createFrame(panel: JPanel) {
        val frame = JFrame("Advent of Code, Day 9: Rope Bridge")
        frame.setSize(FRAME_WIDTH * POINT_WIDTH, FRAME_HEIGHT * POINT_HEIGHT)
        frame.isVisible = true
        frame.defaultCloseOperation = EXIT_ON_CLOSE
        frame.add(panel)
        frame.validate()
    }

    fun getDirection(str: String): Direction {
        return when (str) {
            "R" -> Direction.RIGHT
            "L" -> Direction.LEFT
            "U" -> Direction.UP
            else -> Direction.DOWN
        }
    }

    fun getNextPosition(head: Point, tail: Point): Point {
        return if (head.x < tail.x) {
            if (head.y == tail.y) {
                // Head is up
                Point(tail.x - 1, tail.y)
            } else if (head.y > tail.y) {
                // Head is up-right
                Point(tail.x - 1, tail.y + 1)
            } else {
                // Head is up-left
                Point(tail.x - 1, tail.y - 1)
            }
        } else if (head.x > tail.x) {
            if (head.y == tail.y) {
                // Head is down
                Point(tail.x + 1, tail.y)
            } else if (head.y > tail.y) {
                // Head is down-right
                Point(tail.x + 1, tail.y + 1)
            } else {
                // Head is down-left
                Point(tail.x + 1, tail.y - 1)
            }
        } else {
            if (head.y > tail.y) {
                // Head is right
                Point(tail.x, tail.y + 1)
            } else {
                // Head is left
                Point(tail.x, tail.y - 1)
            }
        }
    }

    fun part1(motions: List<Motion>): Int {
        val headPositions = mutableListOf(Point(0,0))
        val tailPositions = mutableListOf(Point(0,0))
        for (motion in motions) {
            for (unit in 0 until motion.unit) {
                // Move head.
                when (motion.direction) {
                    Direction.RIGHT -> headPositions.add(Point(headPositions.last().x, headPositions.last().y + 1))
                    Direction.LEFT -> headPositions.add(Point(headPositions.last().x, headPositions.last().y - 1))
                    Direction.UP -> headPositions.add(Point(headPositions.last().x - 1, headPositions.last().y))
                    Direction.DOWN -> headPositions.add(Point(headPositions.last().x + 1, headPositions.last().y))
                }
                val head = headPositions.last()
                val tail = tailPositions.last()
                // Move tail if it is at least two spaces away from head.
                if (abs(head.x - tail.x) > 1 ||
                    abs(head.y - tail.y) > 1) {
                    tailPositions.add(getNextPosition(head, tail))
                }
            }
        }
        return tailPositions.toSet().count()
    }

    fun part2(motions: List<Motion>, visualise: Boolean = false): Int {
        // Initialise lists.
        val headPositions = mutableListOf(Point(0,0))
        val knotPositions = mutableListOf<MutableList<Point>>()
        for (i in 0 until NUM_OF_KNOTS_PART_2) {
            knotPositions.add(mutableListOf(Point(0,0)))
        }
        // If visualisation is asked for, create the panel and frame.
        val panel = if (visualise) KnotPanel(headPositions, knotPositions) else null
        if (visualise) createFrame(panel!!)
        // Move head and tail knots based on the given motions.
        for (motion in motions) {
            for (unit in 0 until motion.unit) {
                // Move head.
                when (motion.direction) {
                    Direction.RIGHT -> headPositions.add(Point(headPositions.last().x, headPositions.last().y + 1))
                    Direction.LEFT -> headPositions.add(Point(headPositions.last().x, headPositions.last().y - 1))
                    Direction.UP -> headPositions.add(Point(headPositions.last().x - 1, headPositions.last().y))
                    Direction.DOWN -> headPositions.add(Point(headPositions.last().x + 1, headPositions.last().y))
                }
                for (i in knotPositions.indices) {
                    val head = if (i == 0) headPositions.last() else knotPositions[i - 1].last()
                    val tail = knotPositions[i].last()
                    // Move tail if it is at least two spaces away from head.
                    if (abs(head.x - tail.x) > 1 ||
                        abs(head.y - tail.y) > 1) {
                        knotPositions[i].add(getNextPosition(head, tail))
                    }
                }
                if (visualise) {
                    Thread.sleep(INTERVAL.toLong())
                    panel!!.headPositions = headPositions.map{it.copy()}.toMutableList()
                    panel.knotPositions = knotPositions.map{ list -> list.map{it.copy()}.toMutableList()}.toMutableList()
                    panel.repaint()
                }
            }
        }
        return knotPositions.last().toSet().count()
    }

    val input = readInputAsStrings("Day09")
        .map{it.split(" ")}
        .map{line -> Motion(getDirection(line.first()), line.last().toInt())}

    println(part1(input))
    println(part2(input))
    println(part2(input, true))
}
