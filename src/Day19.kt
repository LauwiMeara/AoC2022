enum class Element {
    ORE,
    CLAY,
    OBSIDIAN,
    GEODE
}

data class Blueprint (val id: Int, val costs: Map<Element, List<Material>>)
data class Material (val quantity: Int, val element: Element)
data class ElementInfo (var collected: Int, var numRobots: Int, val maxNeededRobots: Int)

const val MINUTES_DAY_19_PART_1 = 24
const val MINUTES_DAY_19_PART_2 = 32

fun main() {
    fun getMaxNeededRobots(blueprint: Blueprint, element: Element): Int {
        return blueprint.costs.values.flatten().filter{it.element == element}.maxOf{it.quantity}
    }

    fun robotCanBeMade(costRobot: List<Material>, scenario: Map<Element, ElementInfo>): Boolean {
        for (material in costRobot) {
            val collectedBeforeThisRound = scenario[material.element]!!.collected - scenario[material.element]!!.numRobots
            if (collectedBeforeThisRound < material.quantity) {
                return false
            }
        }
        return true
    }

    fun hasSimilarScenario(possibleScenarios: List<Map<Element, ElementInfo>>, newScenario: Map<Element, ElementInfo>): Boolean {
        return possibleScenarios.any{scenario ->
            scenario.keys.all{element ->
                scenario[element]!!.numRobots >= newScenario[element]!!.numRobots &&
                        scenario[element]!!.collected >= newScenario[element]!!.collected}}
    }

    fun collectElements(scenario: Map<Element, ElementInfo>) {
        for (element in scenario.keys) {
            scenario[element]!!.collected += scenario[element]!!.numRobots
        }
    }

    fun getNewScenario(scenario: Map<Element, ElementInfo>, blueprint: Blueprint, element: Element): Map<Element, ElementInfo> {
        val newScenario = scenario.map { it.key to it.value.copy() }.toMap()
        for (material in blueprint.costs[element]!!) {
            newScenario[material.element]!!.collected -= material.quantity
        }
        newScenario[element]!!.numRobots += 1
        return newScenario
    }

    fun getMaxGeodes(blueprint: Blueprint, maxMinutes: Int): Int {
        println("----- Blueprint: ${blueprint.id} -----")
        var possibleScenarios = mutableListOf(mapOf(
            Element.ORE to ElementInfo(0, 1, getMaxNeededRobots(blueprint, Element.ORE)),
            Element.CLAY to ElementInfo(0, 0, getMaxNeededRobots(blueprint, Element.CLAY)),
            Element.OBSIDIAN to ElementInfo(0, 0, getMaxNeededRobots(blueprint, Element.OBSIDIAN)),
            Element.GEODE to ElementInfo(0, 0, Int.MAX_VALUE)))

        var minutes = 0
        while (minutes < maxMinutes) {
            minutes++
            println("Minute: $minutes")

            // Greedy search
            when (minutes) {
                maxMinutes - 10 -> possibleScenarios = possibleScenarios.filter{scenario -> scenario[Element.OBSIDIAN]!!.numRobots > 0}.toMutableList()
                maxMinutes - 5 -> {
                    val maxGeodeRobots = possibleScenarios.maxOf { scenario -> scenario[Element.GEODE]!!.numRobots }
                    possibleScenarios = possibleScenarios.filter { scenario -> scenario[Element.GEODE]!!.numRobots == maxGeodeRobots }.toMutableList()
                }
            }

            // Collect elements
            for (scenario in possibleScenarios) {
                collectElements(scenario)
            }

            // Create new robots (if possible)
            val newScenarios = mutableListOf<Map<Element, ElementInfo>>()
            for (scenario in possibleScenarios) {
                for (element in scenario.keys) {
                    if (scenario[element]!!.numRobots < scenario[element]!!.maxNeededRobots && robotCanBeMade(blueprint.costs[element]!!, scenario)) {
                        val newScenario = getNewScenario(scenario, blueprint, element)
                        if (!hasSimilarScenario(possibleScenarios, newScenario)) {
                            newScenarios.add(newScenario)
                        }
                    }
                }
            }
            possibleScenarios += newScenarios
            possibleScenarios = possibleScenarios.distinct().toMutableList()
        }
        return possibleScenarios.maxOf{ scenario -> scenario[Element.GEODE]!!.collected }
    }

    fun part1(input: List<Blueprint>): Int {
        val maxGeodesPerBlueprint = mutableMapOf<Int, Int>()
        for (blueprint in input) {
            maxGeodesPerBlueprint[blueprint.id] = getMaxGeodes(blueprint, MINUTES_DAY_19_PART_1)
        }
        return maxGeodesPerBlueprint.entries.fold(0) { sum, it -> sum + (it.key * it.value) }
    }

    fun part2(input: List<Blueprint>): Int {
        val remainingBlueprints = input.subList(0, 3)
        var productMaxGeodes = 1
        for (blueprint in remainingBlueprints) {
            productMaxGeodes *= getMaxGeodes(blueprint, MINUTES_DAY_19_PART_2)
        }
        return productMaxGeodes
    }

    fun getElement(str: String): Element {
        return when(str) {
            "ore" -> Element.ORE
            "clay" -> Element.CLAY
            "obsidian" -> Element.OBSIDIAN
            else -> Element.GEODE
        }
    }

    val input = readInputAsStrings("Day19")
        .map{line -> line.split(": ", ".")
            .map{sentence -> sentence.split(
                "Blueprint",
                " ",
                "and",
                "Each",
                "costs",
                "ore robot",
                "clay robot",
                "obsidian robot",
                "geode robot"
            )
                .filter{it.isNotEmpty()}
            .filter{it.isNotEmpty()}}}
        .map{Blueprint(
            it[0].single().toInt(),
            mapOf(
                Element.ORE to it[1].chunked(2).map{x -> Material(x.first().toInt(), getElement(x.last()))},
                Element.CLAY to it[2].chunked(2).map{x -> Material(x.first().toInt(), getElement(x.last()))},
                Element.OBSIDIAN to it[3].chunked(2).map{x -> Material(x.first().toInt(), getElement(x.last()))},
                Element.GEODE to it[4].chunked(2).map{x -> Material(x.first().toInt(), getElement(x.last()))}))}

    // println(part1(input))
    println(part2(input))
}