const val startValve = "AA"
const val startMinutesPart1 = 30
const val startMinutesPart2 = 26

data class ValveInfo (val flowRate: Int, val tunnelsTo: List<String>)
data class ValvePath (val pressure: Int, val minutes: Int, val openedValves: List<String>, val path1: List<String>, val path2: List<String> = listOf(""))

fun main() {
    fun getValves(input: List<List<String>>): Map<String, ValveInfo> {
        val valves = mutableMapOf<String, ValveInfo>()
        for (line in input) {
            val id = line[0]
            val flowRate = line[1].toInt()
            val tunnelsTo = mutableListOf<String>()
            for (i in 2 until line.size) {
                tunnelsTo.add(line[i])
            }
            valves[id] = ValveInfo(flowRate, tunnelsTo)
        }
        return valves
    }

    fun getConnectedPaths(valves: Map<String, ValveInfo>, totalPath: ValvePath, individualPath: List<String>): MutableList<ValvePath> {
        val connectedPaths = mutableListOf<ValvePath>()
        val minutes = totalPath.minutes - 1

        val currentValve = individualPath.last()
        if (valves[currentValve]!!.flowRate > 0 && !totalPath.openedValves.contains(currentValve)) {
            // If currentValve does release pressure and hasn't been opened yet, open it.
            connectedPaths.add(
                ValvePath(
                    valves[currentValve]!!.flowRate * minutes,
                    minutes,
                    listOf(currentValve),
                    individualPath + currentValve
                )
            )
        }

        val connectedValves = valves[currentValve]!!.tunnelsTo
        for (connectedValve in connectedValves) {
            // Travel to connectedValve without opening it.
            connectedPaths.add(
                ValvePath(
                    0,
                    minutes,
                    listOf(""),
                    individualPath + connectedValve
                )
            )
        }
        return connectedPaths
    }

    fun getConnectedPathsPart1(valves: Map<String, ValveInfo>, path: ValvePath): List<ValvePath> {
        return getConnectedPaths(valves, path, path.path1)
            .map{
                ValvePath(
                    path.pressure + it.pressure,
                    it.minutes,
                    (path.openedValves + it.openedValves).filter{ valve -> valve.isNotEmpty()},
                    it.path1
                )
            }
    }

    fun getConnectedPathsPart2(valves: Map<String, ValveInfo>, path: ValvePath): List<ValvePath> {
        val connectedPaths = mutableListOf<ValvePath>()
        val connectedPaths1 = getConnectedPaths(valves, path, path.path1)
        val connectedPaths2 = getConnectedPaths(valves, path, path.path2)
        for (cp1 in connectedPaths1) {
            for (cp2 in connectedPaths2) {
                // If both paths open the same valve, continue.
                if (!cp1.openedValves.contains("") && cp1.openedValves == cp2.openedValves) continue
                // Else, combine both paths.
                connectedPaths.add(
                    ValvePath(
                        path.pressure + cp1.pressure + cp2.pressure,
                        cp1.minutes,
                        (path.openedValves + cp1.openedValves + cp2.openedValves).distinct().filter{it.isNotEmpty()},
                        cp1.path1,
                        cp2.path1
                    )
                )
            }
        }
        return connectedPaths
    }

    fun part1(input: List<List<String>>): Int {
        val valves = getValves(input)
        var maxPressure = 0
        var paths = mutableListOf(ValvePath(0, startMinutesPart1, listOf(""), listOf(startValve)))
        while (paths.isNotEmpty()) {
            // Greedy search.
            if (paths.size > 5000) {
                paths.sortByDescending{it.pressure}
                paths = paths.subList(0,500)
            }
            val path = paths.removeFirst()
            if (path.minutes <= 1) {
                // If there is 1 minute or less left, no extra pressure can be released.
                if (path.pressure > maxPressure) {
                    maxPressure = path.pressure
                }
            } else {
                paths.addAll(getConnectedPathsPart1(valves, path))
            }
        }
        return maxPressure
    }

    fun part2(input: List<List<String>>): Int {
        val valves = getValves(input)
        var maxPressure = 0
        var paths = mutableListOf(ValvePath(0, startMinutesPart2, listOf(""), listOf(startValve), listOf(startValve)))
        while (paths.isNotEmpty()) {
            // Greedy search.
            if (paths.size > 10000) {
                paths.sortByDescending{it.pressure}
                paths = paths.subList(0,2000)
            }
            val path = paths.removeFirst()
            if (path.minutes <= 1) {
                // If there is 1 minute or less left, no extra pressure can be released.
                if (path.pressure > maxPressure) {
                    maxPressure = path.pressure
                }
            } else {
                paths.addAll(getConnectedPathsPart2(valves, path))
            }
        }
        return maxPressure
    }

    val input = readInputAsStrings("Day16")
        .map{line -> line.split(
            "Valve ",
            " has flow rate=",
            "; tunnels lead to valve ",
            "; tunnels lead to valves ",
            "; tunnel leads to valve ",
            "; tunnel leads to valves ",
            ", ")
            .filter{it.isNotEmpty()}}

    println(part1(input))
    println(part2(input))
}
