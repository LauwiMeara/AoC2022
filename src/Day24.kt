enum class Symbol {
    BLIZZARD_RIGHT,
    BLIZZARD_DOWN,
    BLIZZARD_LEFT,
    BLIZZARD_UP,
    ELF
}

fun main() {
    fun printGrid(grid:Map<Point, MutableList<Symbol>>) {
        // Make gridArray
        val gridArray = Array(grid.keys.maxOf{it.y} + 1) { CharArray(grid.keys.maxOf{it.x} + 2) }
        for (entry in grid) {
            if (entry.value.isEmpty()) {
                gridArray[entry.key.y][entry.key.x] = '.'
            } else if (entry.value.size > 1) {
                gridArray[entry.key.y][entry.key.x] = entry.value.size.digitToChar()
            } else when (entry.value.first()) {
                Symbol.BLIZZARD_RIGHT -> gridArray[entry.key.y][entry.key.x] = '>'
                Symbol.BLIZZARD_DOWN -> gridArray[entry.key.y][entry.key.x] = 'v'
                Symbol.BLIZZARD_LEFT -> gridArray[entry.key.y][entry.key.x] = '<'
                Symbol.BLIZZARD_UP -> gridArray[entry.key.y][entry.key.x] = '^'
                Symbol.ELF -> gridArray[entry.key.y][entry.key.x] = 'E'
            }
        }
        // Print gridArray
        for (y in gridArray.indices) {
            for (x in gridArray[y].indices) {
                if (gridArray[y][x] == Char.MIN_VALUE) {
                    print('#')
                } else {
                    print(gridArray[y][x])
                }
            }
            println()
        }
        println()
    }

    fun getGrid(input: List<String>): Map<Point, MutableList<Symbol>> {
        val grid = mutableMapOf<Point, MutableList<Symbol>>()
        for (y in input.indices) {
            for (x in input[y].indices) {
                when (input[y][x]) {
                    '.' -> grid[Point(x, y)] = mutableListOf()
                    '>' -> grid[Point(x, y)] = mutableListOf (Symbol.BLIZZARD_RIGHT)
                    'v' -> grid[Point(x, y)] = mutableListOf(Symbol.BLIZZARD_DOWN)
                    '<' -> grid[Point(x, y)] = mutableListOf(Symbol.BLIZZARD_LEFT)
                    '^' -> grid[Point(x, y)] = mutableListOf(Symbol.BLIZZARD_UP)
                }
            }
        }
        val startPosition = grid.filter{it.key.y == 0 && it.value.isEmpty()}.keys.single()
        grid[startPosition]!!.add(Symbol.ELF)
        return grid
    }

    fun getNeighbours(grid: Map<Point, MutableList<Symbol>>, position: Point): List<Point> {
        val neighbours = mutableListOf<Point>()
        val (x, y) = position
        if (grid.keys.contains(Point(x + 1, y))) neighbours.add(Point(x + 1, y))
        if (grid.keys.contains(Point(x, y + 1))) neighbours.add(Point(x, y + 1))
        if (grid.keys.contains(Point(x - 1, y))) neighbours.add(Point(x - 1, y))
        if (grid.keys.contains(Point(x, y - 1))) neighbours.add(Point(x, y - 1))
        return neighbours
    }

    fun getGridAfterMovingSymbols(grid: Map<Point, MutableList<Symbol>>): MutableMap<Point, MutableList<Symbol>> {
        // Create newGrid with the same keys as grid
        val newGrid = mutableMapOf<Point, MutableList<Symbol>>()
        for (key in grid.keys) {
            newGrid[key] = mutableListOf()
        }
        // Fill newGrid with new values
        for (position in grid) {
            for (symbol in position.value) {
                when (symbol) {
                    Symbol.BLIZZARD_RIGHT -> {
                        val (x, y) = position.key
                        if (grid.keys.contains(Point(x + 1, y))) {
                            newGrid[Point(x + 1, y)]!!.add(symbol)
                        } else {
                            newGrid[Point(grid.keys.minOf{it.x}, y)]!!.add(symbol)
                        }
                    }

                    Symbol.BLIZZARD_DOWN -> {
                        val (x, y) = position.key
                        if (grid.keys.contains(Point(x, y + 1))) {
                            newGrid[Point(x, y + 1)]!!.add(symbol)
                        } else {
                            if (grid.keys.contains(Point(x, grid.keys.minOf{it.y}))) {
                                newGrid[Point(x, grid.keys.minOf{it.y})]!!.add(symbol)
                            } else {
                                newGrid[Point(x, grid.keys.minOf{it.y} + 1)]!!.add(symbol)
                            }
                        }
                    }

                    Symbol.BLIZZARD_LEFT -> {
                        val (x, y) = position.key
                        if (grid.keys.contains(Point(x - 1, y))) {
                            newGrid[Point(x - 1, y)]!!.add(symbol)
                        } else {
                            newGrid[Point(grid.keys.maxOf{it.x}, y)]!!.add(symbol)
                        }
                    }

                    Symbol.BLIZZARD_UP -> {
                        val (x, y) = position.key
                        if (grid.keys.contains(Point(x, y - 1))) {
                            newGrid[Point(x, y - 1)]!!.add(symbol)
                        } else {
                            if (grid.keys.contains(Point(x, grid.keys.maxOf{it.y}))) {
                                newGrid[Point(x, grid.keys.maxOf{it.y})]!!.add(symbol)
                            } else {
                                newGrid[Point(x, grid.keys.maxOf{it.y} - 1)]!!.add(symbol)
                            }
                        }
                    }

                    Symbol.ELF -> {
                        if (newGrid[position.key]!!.isEmpty()) newGrid[position.key]!!.add(symbol)
                        val neighbours = getNeighbours(grid, position.key)
                        for (neighbour in neighbours) {
                            if (newGrid[neighbour]!!.isEmpty()) {
                                newGrid[neighbour]!!.add(symbol)
                            }
                        }
                    }
                }
            }
        }
        return newGrid
    }

    fun removeElvesWhereBlizzardsAre(grid: Map<Point, MutableList<Symbol>>) {
        for (position in grid) {
            if (position.value.size > 1 && position.value.contains(Symbol.ELF)) {
                position.value.remove(Symbol.ELF)
            }
        }
    }

    fun removeAllElves(grid: Map<Point, MutableList<Symbol>>) {
        for (entry in grid) {
            if (entry.value.contains(Symbol.ELF)) {
                entry.value.remove(Symbol.ELF)
            }
        }
    }

    fun getGridAndMinutesToReachEndPosition(initGrid: Map<Point, MutableList<Symbol>>, goalPosition: Point, print: Boolean): Pair<Map<Point, MutableList<Symbol>>, Int> {
        var grid = initGrid
        var minute = 0
        while (true) {
            minute++
            grid = getGridAfterMovingSymbols(grid)
            removeElvesWhereBlizzardsAre(grid)
            if (print) {
                println("Minute: $minute")
                printGrid(grid)
            }
            if (grid[goalPosition]!!.contains(Symbol.ELF)) break
        }
        removeAllElves(grid)
        grid[goalPosition]!!.add(Symbol.ELF)
        return Pair(grid, minute)
    }

    fun part1(input: List<String>, print: Boolean = false): Int {
        val grid = getGrid(input)
        val endPosition = grid.filter{it.key.y == grid.keys.maxOf{jt -> jt.y} && it.value.isEmpty()}.keys.single()
        val (_, minutes) = getGridAndMinutesToReachEndPosition(grid, endPosition, print)
        return minutes
    }

    fun part2(input: List<String>, print: Boolean = false): Int {
        var grid = getGrid(input)
        val startPosition = grid.filter{it.key.y == 0}.keys.single()
        val endPosition = grid.filter{it.key.y == grid.keys.maxOf{position -> position.y}}.keys.single()
        // To endPosition
        val (g1, m1) = getGridAndMinutesToReachEndPosition(grid, endPosition, print)
        grid = g1
        // To startPosition
        val (g2, m2) = getGridAndMinutesToReachEndPosition(grid, startPosition, print)
        grid = g2
        // To endPosition
        val (g3, m3) = getGridAndMinutesToReachEndPosition(grid, endPosition, print)
        grid = g3
        return m1 + m2 + m3
    }

    val input = readInputAsStrings("Day24")

    println(part1(input))
    println(part2(input))
}