import kotlin.math.abs

const val ROUNDS_DAY_23 = 10

data class Elf(var pos: Point, var proposedPos: Point)

fun main() {
    fun getElves(input: List<String>): List<Elf> {
        val elves = mutableListOf<Elf>()
        for (y in input.indices) {
            for (x in input[y].indices) {
                if (input[y][x] == '#') {
                    val currentPosition = Point(x, y)
                    elves.add(Elf(currentPosition, currentPosition))
                }
            }
        }
        return elves.toList()
    }

    fun getNeighbours(pos: Point): MutableMap<String, Point> {
        val neighbours = mutableMapOf<String, Point>()
        neighbours["NW"] = Point(pos.x - 1, pos.y - 1)
        neighbours["N"] = Point(pos.x, pos.y - 1)
        neighbours["NE"] = Point(pos.x + 1, pos.y -1)
        neighbours["SW"] = Point(pos.x - 1, pos.y + 1)
        neighbours["S"] = Point(pos.x, pos.y + 1)
        neighbours["SE"] = Point(pos.x + 1, pos.y + 1)
        neighbours["W"] = Point(pos.x - 1, pos.y)
        neighbours["E"] = Point(pos.x + 1, pos.y)
        return neighbours
    }

    fun canMove(elves: List<Elf>, neighbours: MutableMap<String, Point>, n1: String, n2: String, n3: String): Boolean {
        return elves.none{it.pos == neighbours[n1]} &&
                elves.none{it.pos == neighbours[n2]} &&
                elves.none{it.pos == neighbours[n3]}
    }

    fun getProposedPosition(elves: List<Elf>, pos: Point, neighbours: MutableMap<String, Point>, direction: Direction): Point {
        return when (direction) {
            Direction.UP -> {
                if (canMove(elves, neighbours, "NW", "N", "NE")) {
                    neighbours["N"]!!
                } else if (canMove(elves, neighbours, "SW", "S", "SE")) {
                    neighbours["S"]!!
                } else if (canMove(elves, neighbours, "NW", "W", "SW")) {
                    neighbours["W"]!!
                } else if (canMove(elves, neighbours, "NE", "E", "SE")) {
                    neighbours["E"]!!
                } else {
                    pos
                }
            }

            Direction.DOWN -> {
                if (canMove(elves, neighbours, "SW", "S", "SE")) {
                    neighbours["S"]!!
                } else if (canMove(elves, neighbours, "NW", "W", "SW")) {
                    neighbours["W"]!!
                } else if (canMove(elves, neighbours, "NE", "E", "SE")) {
                    neighbours["E"]!!
                } else if (canMove(elves, neighbours, "NW", "N", "NE")){
                    neighbours["N"]!!
                } else {
                    pos
                }
            }

            Direction.LEFT -> {
                if (canMove(elves, neighbours, "NW", "W", "SW")) {
                    neighbours["W"]!!
                } else if (canMove(elves, neighbours, "NE", "E", "SE")) {
                    neighbours["E"]!!
                } else if (canMove(elves, neighbours, "NW", "N", "NE")) {
                    neighbours["N"]!!
                } else if (canMove(elves, neighbours, "SW", "S", "SE")){
                    neighbours["S"]!!
                } else {
                    pos
                }
            }

            Direction.RIGHT -> {
                if (canMove(elves, neighbours, "NE", "E", "SE")) {
                    neighbours["E"]!!
                } else if (canMove(elves, neighbours, "NW", "N", "NE")) {
                    neighbours["N"]!!
                } else if (canMove(elves, neighbours, "SW", "S", "SE")) {
                    neighbours["S"]!!
                } else if (canMove(elves, neighbours, "NW", "W", "SW")){
                    neighbours["W"]!!
                } else {
                    pos
                }
            }
        }
    }

    fun printElves(elves: List<Elf>) {
        val minX = elves.minOf{it.pos.x}
        val maxX = elves.maxOf{it.pos.x}
        val minY = elves.minOf{it.pos.y}
        val maxY = elves.maxOf{it.pos.y}
        val array = Array(maxY + abs(minY) + 1) {CharArray(maxX + abs(minX) + 1)}
        for (elf in elves) {
            array[elf.pos.y + abs(minY)][elf.pos.x + abs(minX)] = '#'
        }
        for (y in array.indices) {
            for (x in array[y].indices) {
                if (array[y][x] == Char.MIN_VALUE) {
                    print('.')
                } else {
                    print(array[y][x])
                }
            }
            println()
        }
        println()
    }

    fun proposePositions(directions: List<Direction>, round: Int, elves: List<Elf>) {
        val direction = directions[(round - 1) % directions.size]
        for (elf in elves) {
            val neighbours = getNeighbours(elf.pos)
            if (elves.none { e -> neighbours.any { it.value == e.pos } }) continue
            elf.proposedPos = getProposedPosition(elves, elf.pos, neighbours, direction)
        }
    }

    fun moveElves(elves: List<Elf>) {
        for (elf in elves) {
            if (elves.filter { e -> e.proposedPos == elf.proposedPos }.size <= 1) {
                elf.pos = elf.proposedPos
            }
        }
        // If elf hasn't moved, set proposedPosition to its current position.
        for (elf in elves) {
            if (elf.pos != elf.proposedPos) {
                elf.proposedPos = elf.pos
            }
        }
    }

    fun part1(input: List<String>, print: Boolean = false): Int {
        val directions = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
        val elves = getElves(input)
        if (print) printElves(elves)
        for (round in 1..ROUNDS_DAY_23) {
            // First half: propose positions.
            proposePositions(directions, round, elves)
            // Second half: move elf if no other elf proposed the same position.
            moveElves(elves)
            if (print) printElves(elves)
        }

        val minX = elves.minOf{it.pos.x}
        val maxX = elves.maxOf{it.pos.x}
        val minY = elves.minOf{it.pos.y}
        val maxY = elves.maxOf{it.pos.y}

        return (maxX - minX + 1) * (maxY - minY + 1) - elves.size
    }

    fun part2(input: List<String>, print: Boolean = false): Int {
        val directions = listOf(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT)
        val elves = getElves(input)
        var round = 0
        while (true) {
            round++
            if (print) println("Round $round")
            // First half: propose positions.
            proposePositions(directions, round, elves)
            if (elves.all{it.pos == it.proposedPos}) break
            // Second half: move elf if no other elf proposed the same position.
            moveElves(elves)
        }
        return round
    }

    val input = readInputAsStrings("Day23")

    println(part1(input))
    println(part2(input))
}