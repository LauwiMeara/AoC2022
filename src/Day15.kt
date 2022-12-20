import kotlin.concurrent.thread
import kotlin.math.abs

const val Y_PART_1 = 2000000
const val MAX_XY_PART_2 = 4000000

fun main() {
    fun part1(input: List<List<Point>>): Int {
        val xSensors = input.map{it.first()}.filter{it.y == Y_PART_1}.map{it.x}
        val xBeacons = input.map{it.last()}.filter{it.y == Y_PART_1}.map{it.x}.distinct()
        val xCannotContainBeacon = mutableListOf<Int>()
        xCannotContainBeacon.addAll(xSensors)
        for (line in input) {
            val sensor = line.first()
            val beacon = line.last()
            val manhattanDistance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
            // If we calculate the distanceToY, we know the leftover range on the x-axis where the beacon cannot be.
            val distanceToY = if (sensor.y > Y_PART_1) sensor.y - Y_PART_1 else Y_PART_1 - sensor.y
            val distanceToX = manhattanDistance - distanceToY
            if (distanceToX >= 0) {
                for (x in sensor.x - distanceToX..sensor.x + distanceToX) {
                    if (!xBeacons.contains(x)) {
                        xCannotContainBeacon.add(x)
                    }
                }
            }
        }
        return xCannotContainBeacon.distinct().count()
    }

    fun part2(input: List<List<Point>>): Long {
        val chunkSize = MAX_XY_PART_2 / NUM_THREADS
        for (i in 0 until NUM_THREADS) {
           thread {
               for (y in i * chunkSize..(i+1) * chunkSize) {
                   val xSensors = input.map{it.first()}.filter{it.y == y}.map{it.x}
                   val xBeacons = input.map{it.last()}.filter{it.y == y}.map{it.x}.distinct()
                   val xCannotContainBeacon = BooleanArray(MAX_XY_PART_2 + 1)
                   for (x in xSensors) {
                       xCannotContainBeacon[x] = true
                   }
                   for (x in xBeacons) {
                       xCannotContainBeacon[x] = true
                   }
                   for (line in input) {
                       val sensor = line.first()
                       val beacon = line.last()
                       val manhattanDistance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)
                       // If we calculate the distanceToY, we know the leftover range on the x-axis where the beacon cannot be.
                       val distanceToY = if (sensor.y > y) sensor.y - y else y - sensor.y
                       val distanceToX = manhattanDistance - distanceToY
                       if (distanceToX >= 0) {
                           for (x in sensor.x - distanceToX..sensor.x + distanceToX) {
                               if (x in 0..MAX_XY_PART_2) {
                                   xCannotContainBeacon[x] = true
                               }
                           }
                       }
                   }
                   val x = xCannotContainBeacon.indexOfFirst { !it }
                   // If there is a true x (that cannot contain a beacon), print result.
                   if (x != -1) {
                       println("FINAL X: $x")
                       println("FINAL Y: $y")
                       println("RESULT: ${x.toLong() * MAX_XY_PART_2 + y}")
                       return@thread
                   }
               }
           }
        }
        return 0
    }

    val input = readInputAsStrings("Day15")
        .map{line -> line.split(": ")
            .map{xy -> xy.split("Sensor at x=", ", y=", "closest beacon is at x=")
                .filter{it.isNotEmpty()}}
            .map{Point(it.first().toInt(), it.last().toInt())}}

    println(part1(input))
    println(part2(input))
}