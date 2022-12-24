const val TILE_SIZE = 50

fun main() {
    fun reverse(xOrY: Int): Int {
        return TILE_SIZE - 1 - (xOrY % TILE_SIZE)
    }

    fun getBoardLocations(input: List<String>): MutableMap<Point, Boolean> {
        val board = mutableMapOf<Point, Boolean>()
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == '.') {
                    board[Point(x, y)] = true
                } else if (input[y][x] == '#') {
                    board[Point(x, y)] = false
                }
            }
        }
        return board
    }

    /* 
    Only works for cube with following format:
      1 2
      3
    4 5
    6
     */
    fun getNextPositionAndDirection(board: MutableMap<Point, Boolean>,
                                    direction: Direction,
                                    position: Point,
                                    isPart2: Boolean = false,
                                    print: Boolean = false): Pair<Point, Direction> {
        var nextPosition = position
        var nextDirection = direction
        when (direction) {
            Direction.RIGHT -> {
                nextPosition = Point(position.x + 1, position.y)
                if (!board.containsKey(nextPosition)) {
                    if (isPart2) {
                        if (position.y < TILE_SIZE) {
                            // Board 2 to 5
                            nextPosition = Point((TILE_SIZE * 2) - 1, (TILE_SIZE * 2) + reverse(position.y))
                            nextDirection = Direction.LEFT
                        } else if (position.y in TILE_SIZE until (TILE_SIZE * 2)) {
                            // Board 3 to 2
                            nextPosition = Point((TILE_SIZE * 2) + (position.y % TILE_SIZE), TILE_SIZE - 1)
                            nextDirection = Direction.UP
                        } else if (position.y in (TILE_SIZE * 2) until (TILE_SIZE * 3)) {
                            // Board 5 to 2
                            nextPosition = Point((TILE_SIZE * 3) - 1, reverse(position.y))
                            nextDirection = Direction.LEFT
                        } else {
                            // Board 6 to 5
                            nextPosition = Point(TILE_SIZE + (position.y % TILE_SIZE), (TILE_SIZE * 3) - 1)
                            nextDirection = Direction.UP
                        }
                        if (print) println("$position, $direction, $nextPosition, $nextDirection")
                    } else {
                        nextPosition = Point(board.keys.filter { it.y == position.y }.minOf { it.x }, position.y)
                    }
                }
            }

            Direction.DOWN -> {
                nextPosition = Point(position.x, position.y + 1)
                if (!board.containsKey(nextPosition)) {
                    if (isPart2) {
                        if (position.x < TILE_SIZE) {
                            // Board 6 to 2
                            nextPosition = Point((TILE_SIZE * 2) + position.x, 0)
                            nextDirection = Direction.DOWN
                        } else if (position.x in TILE_SIZE until (TILE_SIZE * 2)) {
                            // Board 5 to 6
                            nextPosition = Point(TILE_SIZE - 1, (TILE_SIZE * 3) + (position.x % TILE_SIZE))
                            nextDirection = Direction.LEFT
                        } else {
                            // Board 2 to 3
                            nextPosition = Point((TILE_SIZE * 2) - 1, TILE_SIZE + (position.x % TILE_SIZE))
                            nextDirection = Direction.LEFT
                        }
                        if (print) println("$position, $direction, $nextPosition, $nextDirection")
                    } else {
                        nextPosition = Point(position.x, board.keys.filter { it.x == position.x }.minOf { it.y })
                    }
                }
            }

            Direction.LEFT -> {
                nextPosition = Point(position.x - 1, position.y)
                if (!board.containsKey(nextPosition)) {
                    if (isPart2) {
                        if (position.y < TILE_SIZE) {
                            // Board 1 to board 4
                            nextPosition = Point(0, (TILE_SIZE * 2) + reverse(position.y))
                            nextDirection = Direction.RIGHT
                        } else if (position.y in TILE_SIZE until (TILE_SIZE * 2)) {
                            // Board 3 to 4
                            nextPosition = Point(position.y % TILE_SIZE, TILE_SIZE * 2)
                            nextDirection = Direction.DOWN
                        } else if (position.y in (TILE_SIZE * 2) until (TILE_SIZE * 3)) {
                            // Board 4 to 1
                            nextPosition = Point(TILE_SIZE, reverse(position.y))
                            nextDirection = Direction.RIGHT
                        } else {
                            // Board 6 to 1
                            nextPosition = Point(TILE_SIZE + (position.y % TILE_SIZE), 0)
                            nextDirection = Direction.DOWN
                        }
                        if (print) println("$position, $direction, $nextPosition, $nextDirection")
                    } else {
                        nextPosition = Point(board.keys.filter { it.y == position.y }.maxOf { it.x }, position.y)
                    }
                }
            }

            Direction.UP -> {
                nextPosition = Point(position.x, position.y - 1)
                if (!board.containsKey(nextPosition)) {
                    if (isPart2) {
                        if (position.x < TILE_SIZE) {
                            // Board 4 to 3
                            nextPosition = Point(TILE_SIZE, TILE_SIZE + position.x)
                            nextDirection = Direction.RIGHT
                        } else if (position.x in TILE_SIZE until (TILE_SIZE * 2)) {
                            // Board 1 to 6
                            nextPosition = Point(0, (TILE_SIZE * 3) + (position.x % TILE_SIZE))
                            nextDirection = Direction.RIGHT
                        } else {
                            // Board 2 to 6
                            nextPosition = Point(position.x % TILE_SIZE, (TILE_SIZE * 4) - 1)
                            nextDirection = Direction.UP
                        }
                        if (print) println("$position, $direction, $nextPosition, $nextDirection")
                    } else {
                        nextPosition = Point(position.x, board.keys.filter { it.x == position.x }.maxOf { it.y })
                    }
                }
            }
        }
        return if (board[nextPosition]!!) {
            Pair(nextPosition, nextDirection)
        } else {
            Pair(position, direction)
        }
    }

    fun getLastPositionAndDirection(board: MutableMap<Point, Boolean>, path: String, directions: List<Direction>, isPart2: Boolean = false): Pair<Point, Direction> {
        var path1 = path
        var position = Point(board.keys.filter { it.y == 0 }.minOf { it.x }, 0)
        var direction = Direction.RIGHT
        while (path1.isNotEmpty()) {
            val indexFirstRotation = path1.indexOfFirst { it == 'L' || it == 'R' }
            val lastSteps = indexFirstRotation == -1
            // Step the given number of steps in the given direction.
            val steps = if (lastSteps) path1.toInt() else path1.substring(0, indexFirstRotation).toInt()
            for (i in 1..steps) {
                val (nextPosition, nextDirection) = getNextPositionAndDirection(board, direction, position, isPart2)
                if (nextPosition == position) {
                    break
                } else {
                    position = nextPosition
                    direction = nextDirection
                }
            }
            if (lastSteps) break
            // Change the direction with the given rotation.
            val rotation = path1[indexFirstRotation]
            direction = if (rotation == 'R') {
                directions[(directions.indexOf(direction) + 1) % directions.size]
            } else {
                directions[(directions.indexOf(direction) - 1 + directions.size) % directions.size]
            }
            // Delete the steps and rotation from the path.
            path1 = path1.substring(indexFirstRotation + 1, path1.length)
        }
        return Pair(position, direction)
    }

    fun getPassword(position: Point, direction: Direction): Long {
        val row = position.y + 1
        val col = position.x + 1
        val facing = when (direction) {
            Direction.RIGHT -> 0
            Direction.DOWN -> 1
            Direction.LEFT -> 2
            Direction.UP -> 3
        }

        return (1000L * row) + (4 * col) + facing
    }

    fun part1(input: List<List<String>>): Long {
        val directions = listOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        val board = getBoardLocations(input.first())
        val path = input.last().single()
        val (position, direction) = getLastPositionAndDirection(board, path, directions)
        return getPassword(position, direction)
    }

    fun part2(input: List<List<String>>): Long {
        val directions = listOf(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)
        val board = getBoardLocations(input.first())
        val path = input.last().single()
        val (position, direction) = getLastPositionAndDirection(board, path, directions, true)
        return getPassword(position, direction)
    }

    val input = readInputSplitByDelimiter("Day22","${System.lineSeparator()}${System.lineSeparator()}")
        .map{it.split(System.lineSeparator())}

    println(part1(input))
    println(part2(input))
}
