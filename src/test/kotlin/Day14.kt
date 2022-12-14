import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

object Day14 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(24, compute1(testInput))
        assertEquals(901, compute1(puzzleInput))
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
        while(true) {
            val before = mountain.sandCount
            mountain.addSand(V(500, 0))
            val after = mountain.sandCount
            if (before == after) {
                return after
            }
        }
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    private data class Mountain(
        val rocks: Set<V>,
        private val settledSand: MutableList<V>,
        private val maxY: Int
    ) {
        constructor(rocks: List<V>) : this(rocks.toSet(), mutableListOf(), rocks.maxOf { it.y })

        fun print() {
            for (y in 0..9) {
                for (x in 494..503) {
                    val v = V(x, y)
                    if (rocks.contains(v)) print('#')
                    else if (settledSand.contains(v)) print('o')
                    else print('.')
                }
                println()
            }
        }

        val sandCount get() = settledSand.size

        fun addSand(sand: V) {
            fall(sand)
        }

        private fun fall(sand: V) {
            if (sand.y > maxY) return
            if (!hitStuff(sand.afterFall())) {
                sand.dropDown()
                fall(sand)
            } else if (!hitStuff(sand.afterLeft())) {
                sand.dropLeft()
                fall(sand)
            } else if (!hitStuff(sand.afterRight())) {
                sand.dropRight()
                fall(sand)
            } else {
                // settled
                settledSand.add(sand)
                return
            }
        }

        private fun hitStuff(sand: V): Boolean {
            return (rocks + settledSand).contains(sand)
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

        fun afterLeft(): V {
            return copy(x = x - 1, y = y + 1)
        }

        fun afterRight(): V {
            return copy(x = x + 1, y = y + 1)
        }

        fun dropDown() {
            y++
        }

        fun dropLeft() {
            x--
            y++
        }

        fun dropRight() {
            x++
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
