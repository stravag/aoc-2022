import org.junit.jupiter.api.Test
import kotlin.math.abs
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
        assertEquals(56000011, compute2(testInput, 20))
    }

    @Test
    fun part2() {
        assertEquals(32, compute2(puzzleInput, 4000000))
    }

    @Test
    fun tests() {
        val map = parse(testInput)
        val pair1 = map.pairs[2]
        pair1.noBeaconPoints(2)


        val pair = map.pairs[6]
        //assertEquals(0, pair.noBeaconPoints(17).count())
        //assertEquals(0, pair.noBeaconPoints(-3).count())
        //assertEquals(18, pair.noBeaconPoints(7).count())
        //assertEquals(1, pair.noBeaconPoints(16).count())
        assertEquals(12, pair.noBeaconPoints(10).count())
    }

    private fun compute1(input: List<String>, row: Int): Int {
        return parse(input).noBeaconsPositions(row).count()
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
            SB(S(sX, sY), B(bX, bY))
        }
        return Map(pairs)
    }

    private fun compute2(input: List<String>, space: Int): Int {
        val beacon = parse(input).findBeacon(space)
        return beacon.x * 4000000 + beacon.y
    }

    data class SB(val s: S, val b: B) {
        fun noBeaconPoints(row: Int): List<P> {
            val xDiff = abs(b.x - s.x)
            val yDiff = abs(b.y - s.y)
            val maxDiff = xDiff + yDiff
            val yDiffToRow = abs(row - s.y)
            return if (yDiffToRow > maxDiff) {
                emptyList()
            } else {
                val xDiffOnRow = abs(maxDiff - yDiffToRow)
                val xRangeOnRow = (s.x - xDiffOnRow)..(s.x + xDiffOnRow)
                val pointsOnRow = xRangeOnRow.map { x -> P(x, row) }
                pointsOnRow - s.p - b.p
            }
        }
    }

    data class S(val p: P) {
        constructor(x: Int, y: Int) : this(P(x, y))

        val x get() = p.x
        val y get() = p.y
    }

    data class B(val p: P) {
        constructor(x: Int, y: Int) : this(P(x, y))

        val x get() = p.x
        val y get() = p.y
    }

    data class P(val x: Int, val y: Int)

    class Map(
        val pairs: List<SB>,
    ) {
        private val sensorsPos: Set<P>
        private val beaconsPos: Set<P>

        init {
            val (s, b) = pairs
                .flatMap { listOf(it.s, it.b) }
                .partition { it is S }

            sensorsPos = s.map { (it as S).p }.toSet()
            beaconsPos = b.map { (it as B).p }.toSet()
        }

        fun noBeaconsPositions(row: Int): Set<P> {
            return pairs
                .fold(mutableSetOf()) { acc, it ->
                    acc.addAll(it.noBeaconPoints(row))
                    acc
                }
        }

        fun noBeaconsPositions2(row: Int, space: Int): Set<P> {
            return pairs
                .fold(mutableSetOf()) { acc, it ->
                    acc.addAll(it.noBeaconPoints(row))
                    acc
                }
        }

        fun findBeacon(space: Int): P {
            for (y in 0..space) {
                val noBeacons = noBeaconsPositions(y)
                for (x in 0..space) {
                    val p = P(x, y)
                    if (!noBeacons.contains(p) && !sensorsPos.contains(p) && !beaconsPos.contains(p)) {
                        return p
                    }
                }
            }
            throw IllegalArgumentException("Couldn't find beacon")
        }
    }
}
