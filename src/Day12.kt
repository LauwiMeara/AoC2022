const val START_LETTER = 'S'
const val END_LETTER = 'E'
const val MIN_ELEVATION_LETTER = 'a'
const val MAX_ELEVATION_LETTER = 'z'
const val NUM_START_NODES = 989 // Found by running part2() once and looking at the size of startNodes.

data class Node(val pos: Point, val letter: Char)

fun main() {
    fun initialiseUnvisitedNodes(input: List<Node>, startNode: Node): MutableMap<Node, Int> {
        val unvisitedNodes = mutableMapOf<Node, Int>()
        for (node in input) {
            unvisitedNodes[node] = if (node == startNode) 0 else Int.MAX_VALUE
        }
        return unvisitedNodes
    }

    // Dijkstra's algorithm
    fun getCostPerVisitedNode(unvisitedNodes: MutableMap<Node, Int>, startNode: Node): MutableMap<Node, Int> {
        val visitedNodes = mutableMapOf<Node, Int>()
        while(true) {
            // Get currentNode.
            val minCost = unvisitedNodes.values.min()
            if (minCost == Int.MAX_VALUE) break // END_LETTER cannot be reached.
            val currentNode = unvisitedNodes.entries.first { it.value == minCost }.key
            unvisitedNodes.remove(currentNode)
            visitedNodes[currentNode] = minCost

            // If currentNode contains END_LETTER, stop searching.
            if (currentNode.letter == END_LETTER) { break }

            // Update the cost for all neighbouring nodes (nextNodes).
            val neighbourPositions = listOf(
                Point(currentNode.pos.x - 1, currentNode.pos.y),
                Point(currentNode.pos.x, currentNode.pos.y + 1),
                Point(currentNode.pos.x + 1, currentNode.pos.y),
                Point(currentNode.pos.x, currentNode.pos.y - 1)
            )
            for (nPos in neighbourPositions) {
                val nextNodeList = unvisitedNodes.keys.filter { it.pos == nPos }
                if (nextNodeList.isEmpty()) continue
                val nextNode = nextNodeList.single()

                // We can only reach the nextNode with the following conditions:
                // - NextNode cannot be startNode.
                // - If currentNode contains START_LETTER, nextNode must be 'a'.
                // - If nextNode contains END_LETTER, currentNode must be 'z'.
                // - For all other cases: nextNode is at most one higher than currentNode.
                if (nextNode != startNode &&
                    ((currentNode.letter == START_LETTER && nextNode.letter == MIN_ELEVATION_LETTER) ||
                    (nextNode.letter == END_LETTER && currentNode.letter == MAX_ELEVATION_LETTER) ||
                    (nextNode.letter != END_LETTER && nextNode.letter - currentNode.letter <= 1))) {
                    unvisitedNodes[nextNode] = minCost + 1
                }
            }
        }
        return visitedNodes
    }

    fun part1(input: List<Node>): Int {
        val startNode = input.single{it.letter == START_LETTER}
        val unvisitedNodes = initialiseUnvisitedNodes(input, startNode)

        // Search for the shortest path by getting the lowest cost from the startNode to all visitedNodes, until END_LETTER is found or cannot be reached.
        val visitedNodes = getCostPerVisitedNode(unvisitedNodes, startNode)

        // Return the cost of the node containing END_LETTER.
        return visitedNodes.entries.first{it.key.letter == END_LETTER}.value
    }

    fun part2(input: List<Node>, printProgress: Boolean = true): Int {
        val startNodes = input.filter{it.letter == START_LETTER || it.letter == MIN_ELEVATION_LETTER}
        val possibleCosts = mutableListOf<Int>()

        // Search for the shortest path by getting the lowest cost from the startNode to all visitedNodes, until END_LETTER is reached or cannot be reached.
        for ((i, startNode) in startNodes.withIndex()) {
            if (printProgress) {
                println("Iteration: $i/$NUM_START_NODES")
            }
            val unvisitedNodes = initialiseUnvisitedNodes(input, startNode)
            val visitedNodes = getCostPerVisitedNode(unvisitedNodes, startNode)

            // If END_LETTER is reached, add the cost of the node to the list of possible costs.
            if (visitedNodes.keys.any{it.letter == END_LETTER}) {
                val cost = visitedNodes.entries.single{it.key.letter == END_LETTER}.value
                if (printProgress) {
                    println("Cost: $cost")
                }
                possibleCosts.add(cost)
            }
        }

        // Return the lowest possible cost.
        return possibleCosts.filter{it > 0}.min()
    }

    val input = readInputAsStrings("Day12").mapIndexed{x, line -> line.mapIndexed{y, letter -> Node(Point(x, y), letter)}}.flatten()

    println(part1(input))
    println(part2(input))
}
