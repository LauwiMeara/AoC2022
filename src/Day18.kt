const val OFFSET_DAY_18 = 1

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

// Breadth-first search
fun hasPathToBorder(grid:Array<Array<BooleanArray>>, startNode: Cube): Boolean {
    val visitedNodes = mutableListOf<Cube>()
    val queue = ArrayDeque<Cube>()
    queue.add(startNode)
    while (queue.isNotEmpty()) {
        val currentNode = queue.removeFirst()
        if (currentNode == Cube(0,0,0)) {
            // There is a path to one of the corners of the grid, which means that startNode isn't in an air pocket.
            return true
        } else {
            val neighbours = getNeighbours(currentNode)
            for (neighbour in neighbours) {
                if (neighbour.x + OFFSET_DAY_18 <= 0 || neighbour.y + OFFSET_DAY_18 <= 0 || neighbour.z + OFFSET_DAY_18 <= 0 ||
                    neighbour.x + OFFSET_DAY_18 >= grid.size - 1 || neighbour.y + OFFSET_DAY_18 >= grid[0].size - 1 || neighbour.z + OFFSET_DAY_18 >= grid[0][0].size - 1) {
                    // There is a path to the border, which means that startNode isn't in an air pocket.
                    return true
                }
                if (!visitedNodes.contains(neighbour) && !grid[neighbour.x + OFFSET_DAY_18][neighbour.y + OFFSET_DAY_18][neighbour.z + OFFSET_DAY_18]) {
                    queue.addFirst(neighbour)
                }
            }
            visitedNodes.add(currentNode)
        }
    }
    // There is no path found, which means that startNode is in an air pocket.
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
        val maxX = input.maxOf{it.x}
        val maxY = input.maxOf{it.y}
        val maxZ = input.maxOf{it.z}
        val grid = Array(maxX + 1 + (2 * OFFSET_DAY_18)) {
            Array(maxY + 1 + (2 * OFFSET_DAY_18)) {
                BooleanArray(maxZ + 1 + (2 * OFFSET_DAY_18))}}
        for (cube in input) {
            grid[cube.x + OFFSET_DAY_18][cube.y + OFFSET_DAY_18][cube.z + OFFSET_DAY_18] = true
        }
        for ((i, cube) in input.withIndex()) {
            if (debug) println("Cube number: ${i + 1}/${input.size}")
            val neighbours = getNeighbours(cube)
            for (neighbour in neighbours) {
                if (!grid[neighbour.x + OFFSET_DAY_18][neighbour.y + OFFSET_DAY_18][neighbour.z + OFFSET_DAY_18] &&
                    hasPathToBorder(grid, Cube(neighbour.x, neighbour.y, neighbour.z))) {
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