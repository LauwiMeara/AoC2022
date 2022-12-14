const val ROCK = '#'
const val SAND = 'o'

fun main() {
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

    fun simulateFallingSand(cave: Array<CharArray>) {
        val startPosition = Point(0, 500)
        var simulate = true
        while (simulate) {
            var x = startPosition.x
            var y = startPosition.y
            var isFalling = true
            while (isFalling) {
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

    fun part1(input: List<List<Point>>): Int {
        val cave = getCaveWithRockPaths(input)
        simulateFallingSand(cave)
        return cave.flatMap{it.asIterable()}.count{it == SAND}
    }

    fun part2(input: List<List<Point>>): Int {
        val cave = getCaveWithRockPaths(input, true)
        simulateFallingSand(cave)
        return cave.flatMap{it.asIterable()}.count{it == SAND}
    }

    val input = readInputAsStrings("Day14")
        .map{line -> line.split(" -> ")
            .map{it.split(",")}
            .map{(Point(it.last().toInt(), it.first().toInt()))}}

    println(part1(input))
    println(part2(input))
}
