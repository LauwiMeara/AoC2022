enum class Shape {
    ROCK, PAPER, SCISSORS
}

enum class Outcome {
    LOSE, DRAW, WIN
}

fun main() {
    val shapes = mapOf(
        "A" to Shape.ROCK,
        "B" to Shape.PAPER,
        "C" to Shape.SCISSORS,
        "X" to Shape.ROCK,
        "Y" to Shape.PAPER,
        "Z" to Shape.SCISSORS)
    val shapesList = listOf(Shape.ROCK, Shape.PAPER, Shape.SCISSORS)
    val outcomes = mapOf(
        "X" to Outcome.LOSE,
        "Y" to Outcome.DRAW,
        "Z" to Outcome.WIN
    )

    fun parseInputPart1(input: List<List<String>>): List<Pair<Shape, Shape>> {
        return input.map{round -> Pair(shapes[round.first()]!!, shapes[round.last()]!!)}
    }

    fun parseInputPart2(input:List<List<String>>): List<Pair<Shape, Outcome>> {
        return input.map{round -> Pair(shapes[round.first()]!!, outcomes[round.last()]!!)}
    }

    fun getOutcome(opponent: Shape, ownShape: Shape): Outcome {
        val opponentIndex = shapesList.indexOf(opponent)
        val ownShapeIndex = shapesList.indexOf(ownShape)
        // There is a relation between the relative index of the shapes and the outcome
        return when (ownShapeIndex - opponentIndex) {
            -1,2 -> Outcome.LOSE
            0 -> Outcome.DRAW
            else -> Outcome.WIN
        }
    }

    fun getShape(opponent: Shape, outcome:Outcome): Shape{
        val opponentIndex = shapesList.indexOf(opponent)
        // There is a relation between the relative index of the shapes and the outcome
        return when (outcome) {
            Outcome.LOSE -> if (opponentIndex - 1 >= 0) shapesList[opponentIndex - 1] else shapesList.last()
            Outcome.DRAW -> shapesList[opponentIndex]
            Outcome.WIN -> if (opponentIndex + 1 < shapesList.size) shapesList[opponentIndex + 1] else shapesList.first()
        }
    }

    fun calculateScoreOutcome(outcome: Outcome): Int {
        return when(outcome) {
            Outcome.LOSE -> 0
            Outcome.DRAW -> 3
            Outcome.WIN -> 6
        }
    }

    fun calculateScoreShape(ownShape: Shape): Int{
        return when(ownShape) {
            Shape.ROCK -> 1
            Shape.PAPER -> 2
            else -> 3
        }
    }

    fun part1(input: List<List<String>>): Int {
        val readableInput = parseInputPart1(input)
        var sum = 0
        for (line in readableInput) {
            val opponent = line.first
            val ownShape = line.second
            val outcome = getOutcome(opponent, ownShape)
            sum += calculateScoreShape(ownShape)
            sum += calculateScoreOutcome(outcome)
        }
        return sum
    }

    fun part2(input: List<List<String>>): Int {
        val readableInput = parseInputPart2(input)
        var sum = 0
        for (line in readableInput) {
            val opponent = line.first
            val outcome = line.second
            val ownShape = getShape(opponent, outcome)
            sum += calculateScoreShape(ownShape)
            sum += calculateScoreOutcome(outcome)
        }
        return sum
    }

    val input = readInputAsStrings("Day02")
        .map{it.split(" ")}

    println(part1(input))
    println(part2(input))
}
