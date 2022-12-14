import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day14 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(2, compute1(testInput))
        assertEquals(147, compute1(puzzleInput))
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

        Mountain(rocks.toSet())
        val sandStart = V(500, 0)
        return input.size
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    private data class Mountain(val rocks: Set<V>) {

    }

    fun getRocks(start: V, end: V): List<V> {
        // fix range
        val xRocks = if (start.x != end.x) (start.x..end.x).map { V(it, start.y) } else emptyList()
        val yRocks = if (start.y != end.y) (start.y..end.y).map { V(start.x, it) } else emptyList()
        return xRocks + yRocks
    }

    data class V(val x: Int, val y: Int) {
        companion object {


            fun of(strings: List<String>): List<V> = strings.map { of(it) }
            private fun of(string: String): V {
                val (x, y) = string.split(",").map { it.toInt() }
                return V(x, y)
            }
        }
    }
}
