import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

object Day14 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(24, compute1(testInput))
    }

    @Test
    fun part1() {
        assertEquals(901, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(93, compute2(testInput))
    }

    @Test
    fun part2() {
        assertEquals(24589, compute2(puzzleInput))
    }


    private fun compute1(input: List<String>): Int {
        val mountain = parse(input)
        while (true) {
            val before = mountain.sandCount
            mountain.addSand(V(500, 0))
            val after = mountain.sandCount
            if (before == after) {
                return after
            }
        }
    }

    private fun compute2(input: List<String>): Int {
        val mountain = parse(input)
        while (true) {
            val before = mountain.sandCount
            mountain.addSandToFloor(V(500, 0))
            val after = mountain.sandCount
            if (before == after) {
                return after
            }
        }
    }

    private fun parse(input: List<String>): Mountain {
        val rocks = input.flatMap { line ->
            line.split(" -> ").windowed(size = 2) {
                val (start, end) = V.of(it)
                val rocks = getRocks(start, end)
                rocks
            }.flatten()
        }

        return Mountain(rocks)
    }

    private data class Mountain(
        val rocks: Set<V>,
        private val settledSand: MutableSet<V>,
        private val maxY: Int
    ) {
        constructor(rocks: List<V>) : this(rocks.toSet(), mutableSetOf(), rocks.maxOf { it.y })

        val sandCount get() = settledSand.size

        fun addSand(sand: V) {
            fall(sand)
        }

        fun addSandToFloor(sand: V) {
            fallToFloor(sand)
        }

        private fun fall(sand: V) {
            if (sand.y > maxY) return
            if (!hitStuff(sand.down())) {
                fall(sand.down())
            } else if (!hitStuff(sand.left())) {
                fall(sand.left())
            } else if (!hitStuff(sand.right())) {
                fall(sand.right())
            } else {
                // settled
                settledSand.add(sand)
                return
            }
        }

        private fun fallToFloor(sand: V) {
            if (settledSand.contains(V(500, 0))) return
            if (!hitStuffOrFloor(sand.down())) {
                fallToFloor(sand.down())
            } else if (!hitStuffOrFloor(sand.left())) {
                fallToFloor(sand.left())
            } else if (!hitStuffOrFloor(sand.right())) {
                fallToFloor(sand.right())
            } else {
                // settled
                settledSand.add(sand)
                return
            }
        }

        private fun hitStuff(sand: V): Boolean {
            return rocks.contains(sand) || settledSand.contains(sand)
        }

        private fun hitStuffOrFloor(sand: V): Boolean {
            return hitStuff(sand) || sand.y == maxY + 2
        }
    }

    private fun getRocks(start: V, end: V): Set<V> {
        // fix range
        val xRocks = (min(start.x, end.x)..max(start.x, end.x)).map { V(it, start.y) }
        val yRocks = (min(start.y, end.y)..max(start.y, end.y)).map { V(start.x, it) }
        return (xRocks + yRocks).toSet()
    }

    data class V(val x: Int, val y: Int) {
        fun down(): V {
            return copy(y = y + 1)
        }

        fun left(): V {
            return copy(x = x - 1, y = y + 1)
        }

        fun right(): V {
            return copy(x = x + 1, y = y + 1)
        }

        companion object {
            fun of(strings: List<String>): List<V> = strings.map { of(it) }
            private fun of(string: String): V {
                val (x, y) = string.split(",").map { it.toInt() }
                return V(x, y)
            }
        }
    }
}
