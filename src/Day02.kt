fun main() {
    fun calculateScoreOutcomePart1(opponent: String, ownShape: String): Int {
        if (opponent == "A") {
            return when(ownShape) {
                "X" -> 3
                "Y" -> 6
                "Z" -> 0
                else -> 0
            }
        } else if (opponent == "B") {
            return when(ownShape) {
                "X" -> 0
                "Y" -> 3
                "Z" -> 6
                else -> 0
            }
        } else {
            return when(ownShape) {
                "X" -> 6
                "Y" -> 0
                "Z" -> 3
                else -> 0
            }
        }
    }

    fun calculateScoreOutcomePart2(outcome: String): Int {
        return when(outcome) {
            "X" -> 0
            "Y" -> 3
            "Z" -> 6
            else -> 0
        }
    }

    fun calculateScoreShape(ownShape: String): Int{
        return when(ownShape) {
            "X" -> 1
            "Y" -> 2
            "Z" -> 3
            else -> 0
        }
    }

    fun getShape(opponent: String, outcome:String): String{
        if (opponent == "A") {
            return when(outcome) {
                "X" -> "Z"
                "Y" -> "X"
                "Z" -> "Y"
                else -> ""
            }
        } else if (opponent == "B") {
            return when(outcome) {
                "X" -> "X"
                "Y" -> "Y"
                "Z" -> "Z"
                else -> ""
            }
        } else {
            return when(outcome) {
                "X" -> "Y"
                "Y" -> "Z"
                "Z" -> "X"
                else -> ""
            }
        }
    }

    fun part1(input: List<List<String>>): Int {
        var sum = 0
        for (line in input) {
            val opponent = line.first()
            val ownShape = line.last()
            sum += calculateScoreShape(ownShape)
            sum += calculateScoreOutcomePart1(opponent, ownShape)
        }
        return sum
    }

    fun part2(input: List<List<String>>): Int {
        var sum = 0
        for (line in input) {
            val opponent = line.first()
            val outcome = line.last()
            val ownShape = getShape(opponent, outcome)
            sum += calculateScoreShape(ownShape)
            sum += calculateScoreOutcomePart2(outcome)
        }
        return sum
    }

    val input = readInputAsStrings("Day02").map{it.split(" ")}

    println(part1(input))
    println(part2(input))
}
