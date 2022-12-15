import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

object Day15 : AbstractDay() {

    /*

                               1    1    2    2
                     0    5    0    5    0    5
                -2 ..........#.................
                -1 .........###................
                 0 ....S...#####...............
                 1 .......#######........S.....
                 2 ......#########S............
                 3 .....###########SB..........
                 4 ....#############...........
                 5 ...###############..........
                 6 ..#################.........
                 7 .#########S#######S#........
                 8 ..#################.........
                 9 ...###############..........
                10 ....B############...........
                11 ..S..###########............
                12 ......#########.............
                13 .......#######..............
                14 ........#####.S.......S.....
                15 B........###................
                16 ..........#SB...............
                17 ................S..........B
                18 ....S.......................
                19 ............................
                20 ............S......S........
                21 ............................
                22 .......................B....

     */

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
        assertEquals(14, compute2(testInput))
    }

    @Test
    fun part2() {
        assertEquals(32, compute2(puzzleInput))
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
        //assertEquals(12, pair.noBeaconPoints(10).count())
    }

    @Test
    fun print() {
        parse(testInput).print(3)
    }

    private fun compute1(input: List<String>, row: Int): Int {
        return parse(input).noBeaconsCount(row)
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

    private fun compute2(input: List<String>): Int {
        return input.size
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
        val xRange: IntRange
        val yRange: IntRange
        val sensors: Set<S>
        val beacons: Set<B>

        init {
            val minX = pairs.minOf { (s, b) -> min(s.x, b.x) }
            val maxX = pairs.maxOf { (s, b) -> max(s.x, b.x) }
            xRange = minX..maxX
            val minY = pairs.minOf { (s, b) -> min(s.y, b.y) }
            val maxY = pairs.maxOf { (s, b) -> max(s.y, b.y) }
            yRange = minY..maxY

            val (s, b) = pairs.flatMap { listOf(it.s, it.b) }
                .partition { it is S }

            sensors = s.map { it as S }.toSet()
            beacons = b.map { it as B }.toSet()
        }

        fun print(pair: Int) {
            val refPair = pairs[pair - 1]
            for (y in yRange) {
                val noBeaconsPos = refPair.noBeaconPoints(y)
                for (x in xRange) {
                    val p = P(x, y)
                    if (sensors.contains(S(p))) print('S')
                    else if (beacons.contains(B(p))) print('B')
                    else if (noBeaconsPos.contains(p)) print('#')
                    else print('.')
                }
                println()
            }
        }

        fun noBeaconsCount(row: Int): Int {
            return pairs
                .fold(mutableSetOf<P>()) { acc, it ->
                    acc.addAll(it.noBeaconPoints(row))
                    acc
                }.count()
        }
    }
}
