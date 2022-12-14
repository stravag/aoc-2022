import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

object Day14 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(2, compute1(testInput))
        //assertEquals(147, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(2, compute2(testInput))
        assertEquals(147, compute2(puzzleInput))
    }


    private fun compute1(input: List<String>): Int {
        val rocks = input.flatMap { line ->
            line.split(" -> ").windowed(size = 2) {
                val (start, end) = V.of(it)
                val rocks = getRocks(start, end)
                rocks
            }.flatten()
        }

        val mountain = Mountain(rocks)
        val sandStart = V(500, 0)
        return input.size
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    private data class Mountain(val rocks: MutableSet<V>) {
        constructor(rocks: List<V>) : this(rocks.toMutableSet())

        fun addSand(sand: V) {

        }

        fun hitsRock(sand: V): Boolean {
            return rocks.contains(sand.afterFall())
        }
    }

    private fun getRocks(start: V, end: V): Set<V> {
        // fix range
        val xRocks = (min(start.x, end.x)..max(start.x, end.x)).map { V(it, start.y) }
        val yRocks = (min(start.y, end.y)..max(start.y, end.y)).map { V(start.x, it) }
        return (xRocks + yRocks).toSet()
    }

    data class V(var x: Int, var y: Int) {
        fun afterFall(): V {
            return copy(y = y + 1)
        }

        fun fall() {
            y++
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
