const val ROOT_MONKEY = "root"
const val HUMN_MONKEY = "humn"

data class Equation(val n1: String, val operation: Char, val n2: String)

fun main() {
    fun getMonkeys(input: List<List<String>>): Pair<MutableMap<String, Long>, MutableMap<String, Equation>> {
        val numberMonkeys = mutableMapOf<String, Long>()
        val equationMonkeys = mutableMapOf<String, Equation>()
        for (line in input) {
            val name = line.first()
            if (line.last().toIntOrNull() != null) {
                numberMonkeys[name] = line.last().toLong()
            } else {
                val (n1, operation, n2) = line.last().split(" ")
                equationMonkeys[name] = Equation(n1, operation.single(), n2)
            }
        }
        return Pair(numberMonkeys, equationMonkeys)
    }

    fun calculateEquation(numberMonkeys: MutableMap<String, Long>, equation: Equation): Long {
        val n1 = numberMonkeys[equation.n1]!!
        val n2 = numberMonkeys[equation.n2]!!
        return when (equation.operation) {
            '+' -> n1 + n2
            '-' -> n1 - n2
            '*' -> n1 * n2
            '/' -> n1 / n2
            else -> if (n1 == n2) 1L else 0L
        }
    }

    /**
     * Transfers equationMonkeys to numberMonkeys. Returns true if numberMonkeys for ROOT_MONKEY's equation are found.
     */
    fun addNumberMonkeysAndFindRootMonkey(equationMonkeys: MutableMap<String, Equation>, numberMonkeys: MutableMap<String, Long>): Boolean {
        val transferredMonkeys = mutableListOf<String>()
        for (monkey in equationMonkeys.entries) {
            val name = monkey.key
            val equation = monkey.value
            if (numberMonkeys.keys.contains(equation.n1) && numberMonkeys.keys.contains(equation.n2)) {
                if (name == ROOT_MONKEY) {
                    return true
                }
                numberMonkeys[name] = calculateEquation(numberMonkeys, equation)
                transferredMonkeys.add(name)
            }
        }
        for (name in transferredMonkeys) {
            equationMonkeys.remove(name)
        }
        return false
    }

    fun part1(input: List<List<String>>): Long {
        val (numberMonkeys, equationMonkeys) = getMonkeys(input)
        while (true) {
            if (addNumberMonkeysAndFindRootMonkey(equationMonkeys, numberMonkeys)) break
        }
        return calculateEquation(numberMonkeys, equationMonkeys[ROOT_MONKEY]!!)
    }

    fun part2(input: List<List<String>>): Long {
        var humnNumber = 1L
        var step = 1000000000000
        var humnNumberGoesUp = true
        var originalEvaluationN1BiggerThanN2 = false
        while (true) {
            val (numberMonkeys, equationMonkeys) = getMonkeys(input)
            numberMonkeys[HUMN_MONKEY] = humnNumber
            while (true) {
                if (addNumberMonkeysAndFindRootMonkey(equationMonkeys, numberMonkeys)) break
            }

            val n1 = numberMonkeys[equationMonkeys[ROOT_MONKEY]!!.n1]!!
            val n2 = numberMonkeys[equationMonkeys[ROOT_MONKEY]!!.n2]!!
            if (n1 == n2) break // If n1 equals n2, the correct humnNumber is found.

            // Binary Search
            if (humnNumber == 1L) {
                originalEvaluationN1BiggerThanN2 = n1 > n2
                humnNumber += step
            } else if (n1 > n2 == originalEvaluationN1BiggerThanN2) {
                if (!humnNumberGoesUp) {
                    humnNumberGoesUp = true
                    step /= 10
                }
                humnNumber += step
            } else {
                if (humnNumberGoesUp) {
                    humnNumberGoesUp = false
                    step /= 10
                }
                humnNumber -= step
            }
        }
        return humnNumber
    }

    val input = readInputAsStrings("Day21")
        .map{it.split(": ")}

    println(part1(input))
    println(part2(input))
}
