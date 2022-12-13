import kotlin.math.abs

const val NUM_OF_KNOTS_PART_2 = 9

enum class Direction {
    RIGHT, LEFT, UP, DOWN
}

data class Motion(val direction: Direction, val unit: Int)

fun main() {
    fun getMotions(input: List<List<String>>): List<Motion> {
        val motions = mutableListOf<Motion>()
        for (line in input) {
            val unit = line.last().toInt()
            when (line.first()) {
                "R" -> motions.add(Motion(Direction.RIGHT, unit))
                "L" -> motions.add(Motion(Direction.LEFT, unit))
                "U" -> motions.add(Motion(Direction.UP, unit))
                else -> motions.add(Motion(Direction.DOWN, unit))
            }
        }
        return motions
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

    fun part1(input: List<List<String>>): Int {
        val motions = getMotions(input)
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

    fun part2(input: List<List<String>>): Int {
        val motions = getMotions(input)
        val headPositions = mutableListOf(Point(0,0))
        val knotPositions = mutableListOf<MutableList<Point>>()
        for (i in 0 until NUM_OF_KNOTS_PART_2) {
            knotPositions.add(mutableListOf(Point(0,0)))
        }
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
            }
        }
        return knotPositions.last().toSet().count()
    }

    val input = readInputAsStrings("Day09").map{it.split(" ")}

    println(part1(input))
    println(part2(input))
}
