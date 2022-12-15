import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

object Day15 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(26, compute1(testInput, 10))
    }

    @Test
    fun part1() {
        assertEquals(5394423, compute1(puzzleInput, 2000000))
    }

    @Test
    fun part2Test() {
        assertEquals(56000011L, compute2(testInput, 20))
    }

    @Test
    fun part2() {
        assertEquals(11840879211051L, compute2(puzzleInput, 4000000))
    }

    private fun compute1(input: List<String>, row: Int): Int {
        return parse(input).noBeaconsPositions(row).count()
    }

    private fun compute2(input: List<String>, space: Int): Long {
        val beacon = parse(input).findBeacon(space)
        return beacon.x * 4000000L + beacon.y
    }

    private fun parse(input: List<String>): Map {
        val regex = "Sensor at x=([\\-0-9]+), y=([\\-0-9]+): closest beacon is at x=([\\-0-9]+), y=([\\-0-9]+)"
            .toRegex()
        val pairs = input.map {
            val matches = regex.find(it) ?: throw IllegalArgumentException("Unexpected input: $it")
            val sX = matches.groupValues[1].toInt()
            val sY = matches.groupValues[2].toInt()
            val bX = matches.groupValues[3].toInt()
            val bY = matches.groupValues[4].toInt()
            Signal(P(sX, sY), P(bX, bY))
        }
        return Map(pairs)
    }

    data class Signal(val s: P, val b: P) {
        fun noBeaconPoints(row: Int): List<P> {
            val searchRadius = searchRadius(this)
            val yDiffToRow = abs(row - s.y)
            return if (yDiffToRow > searchRadius) {
                emptyList()
            } else {
                val xDiffOnRow = abs(searchRadius - yDiffToRow)
                val xRangeOnRow = (s.x - xDiffOnRow)..(s.x + xDiffOnRow)
                val pointsOnRow = xRangeOnRow.map { x -> P(x, row) }
                pointsOnRow - s - b
            }
        }
    }

    fun searchRadius(signal: Signal) = searchRadius(signal.s, signal.b)
    fun searchRadius(p1: P, p2: P) = abs(p1.x - p2.x) + abs(p1.y - p2.y)

    data class P(val x: Int, val y: Int)

    class Map(
        private val pairs: List<Signal>,
    ) {
        fun noBeaconsPositions(row: Int): Set<P> {
            return pairs
                .fold(mutableSetOf()) { acc, it ->
                    acc.addAll(it.noBeaconPoints(row))
                    acc
                }
        }

        fun findBeacon(space: Int): P {
            pairs.forEach { pair ->
                val sensor = pair.s
                val radius = searchRadius(pair)
                val minX = max(0, sensor.x - radius - 1)
                val maxX = min(space, sensor.x + radius + 1)
                for (x in minX..maxX) {
                    val diffX = abs(x - sensor.x)
                    val possibleY1 = sensor.y - (radius - diffX) - 1
                    val possibleY2 = sensor.y + (radius - diffX) + 1
                    listOf(possibleY1, possibleY2).forEach { y ->
                        if (y in (0..space)) {
                            val p = P(x, y)
                            val notInRanges = pairs.none { otherPair ->
                                val otherSensor = otherPair.s
                                val otherRadius = searchRadius(otherPair)
                                searchRadius(p, otherSensor) <= otherRadius
                            }
                            if (notInRanges) {
                                return p
                            }
                        }
                    }
                }
            }
            throw IllegalArgumentException("Couldn't find beacon")
        }
    }
}
