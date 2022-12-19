data class Cube(val x: Int, val y: Int, val z: Int)

fun getNeighbours(cube: Cube): List<Cube> {
    return listOf(
        Cube(cube.x - 1, cube.y, cube.z),
        Cube(cube.x + 1, cube.y, cube.z),
        Cube(cube.x, cube.y - 1, cube.z),
        Cube(cube.x, cube.y + 1, cube.z),
        Cube(cube.x, cube.y, cube.z - 1),
        Cube(cube.x, cube.y, cube.z + 1)
    )
}

fun isNotAirPocket(grid: Array<Array<BooleanArray>>, startNode: Cube): Boolean {
    // If there is a path to one of the eight corners of the grid, the startNode isn't an air pocket.
    val maxX = grid.size - 1
    val maxY = grid[0].size - 1
    val maxZ = grid[0][0].size - 1
    return hasPathToBorder(grid, startNode, Cube(0,0,0)) ||
                hasPathToBorder(grid, startNode, Cube(maxX, 0, 0)) ||
                hasPathToBorder(grid, startNode, Cube(0, maxY, 0)) ||
                hasPathToBorder(grid, startNode, Cube(0, 0, maxZ)) ||
                hasPathToBorder(grid, startNode, Cube(maxX, maxY, 0)) ||
                hasPathToBorder(grid, startNode, Cube(maxX, 0, maxZ)) ||
                hasPathToBorder(grid, startNode, Cube(0, maxY, maxZ)) ||
                hasPathToBorder(grid, startNode, Cube(maxX, maxY, maxZ))
}

// Breadth-first search
fun hasPathToBorder(grid:Array<Array<BooleanArray>>, startNode: Cube, endNode: Cube): Boolean {
    val offset = 1
    val visitedNodes = mutableListOf<Cube>()
    val queue = ArrayDeque<Cube>()
    queue.add(startNode)
    while (queue.isNotEmpty()) {
        val currentNode = queue.removeFirst()
        if (currentNode == endNode) {
            // There is a path to endNode, which means that startNode isn't in an air pocket.
            return true
        } else {
            val neighbours = getNeighbours(currentNode)
            for (neighbour in neighbours) {
                if (neighbour.x + offset <= 0 || neighbour.y + offset <= 0 || neighbour.z + offset <= 0 ||
                    neighbour.x + offset >= grid.size - 1 || neighbour.y + offset >= grid[0].size - 1 || neighbour.z + offset >= grid[0][0].size - 1) {
                    // There is a path to the border, which means that startNode isn't in an air pocket.
                    return true
                }
                if (!visitedNodes.contains(neighbour) && !grid[neighbour.x + offset][neighbour.y + offset][neighbour.z + offset]) {
                    queue.addFirst(neighbour)
                }
            }
            visitedNodes.add(currentNode)
        }
    }
    // There is no path found, which means that startNode might be in an air pocket.
    return false
}

fun main() {
    fun part1(input: List<Cube>): Int {
        var totalExposedSides = 0
        for (cube in input) {
            var exposedSides = 6
            if (input.any{it.x == cube.x && it.y == cube.y && it.z - cube.z == 1}) {
                exposedSides--
            }
            if (input.any{it.x == cube.x && it.y == cube.y && it.z - cube.z == -1}) {
                exposedSides--
            }
            if (input.any{it.x == cube.x && it.z == cube.z && it.y - cube.y == 1}) {
                exposedSides--
            }
            if (input.any{it.x == cube.x && it.z == cube.z && it.y - cube.y == -1}) {
                exposedSides--
            }
            if (input.any{it.y == cube.y && it.z == cube.z && it.x - cube.x == 1}) {
                exposedSides--
            }
            if (input.any{it.y == cube.y && it.z == cube.z && it.x - cube.x == -1}) {
                exposedSides--
            }
            totalExposedSides += exposedSides
        }
        return totalExposedSides
    }

    fun part2(input: List<Cube>, debug: Boolean = false): Int {
        var totalExposedSides = 0
        val offset = 1
        val maxX = input.maxOf{it.x}
        val maxY = input.maxOf{it.y}
        val maxZ = input.maxOf{it.z}
        val grid = Array(maxX + 3) {Array(maxY + 3) {BooleanArray(maxZ + 3)} }
        for (cube in input) {
            grid[cube.x + offset][cube.y + offset][cube.z + offset] = true
        }
        for ((i, cube) in input.withIndex()) {
            if (debug) println("Cube number: $i")
            val neighbours = getNeighbours(cube)
            for ((j, neighbour) in neighbours.withIndex()) {
                if (debug) println("Looking at neighbour $j")
                if (neighbour.x + offset <= 0 || neighbour.y + offset <= 0 || neighbour.z + offset <= 0 ||
                    neighbour.x + offset >= grid.size - 1 || neighbour.y + offset >= grid[0].size - 1 || neighbour.z + offset >= grid[0][0].size - 1) {
                    totalExposedSides += 1
                    continue
                } else if (!grid[neighbour.x + offset][neighbour.y + offset][neighbour.z + offset] && isNotAirPocket(grid, Cube(neighbour.x, neighbour.y, neighbour.z))) {
                    totalExposedSides += 1
                }
            }
        }
        return totalExposedSides
    }

    val input = readInputAsStrings("Day18")
        .map{it.split(",")}
        .map{Cube(it[0].toInt(), it[1].toInt(), it[2].toInt())}

    println(part1(input))
    println(part2(input, true))
}
