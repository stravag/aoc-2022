import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

object Day09 : AbstractDay() {

    @Test
    fun tests() {
        assertEquals(13, compute(testInput, 2))
        assertEquals(6212, compute(puzzleInput, 2))

        assertEquals(1, compute(testInput, 10))
        assertEquals(
            36, compute(
                """
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20
        """.trimIndent().lines(), 10
            )
        )
        assertEquals(2522, compute(puzzleInput, 10))
    }

    private fun compute(input: List<String>, ropeSize: Int): Int {
        val rope = Rope(ropeSize)
        val visited = mutableSetOf(rope.tailPos())
        input
            .map { Step.of(it) }
            .forEach { (direction, steps) ->
                repeat(steps) {
                    rope.move(direction)
                    visited.add(rope.tailPos())
                }
            }

        return visited.size
    }

    private class Rope(
        private val positions: MutableList<Position>
    ) {
        constructor(ropeSize: Int) : this(MutableList(ropeSize) { Position(0, 0) })

        fun move(direction: String) {
            val newHead = positions.first().move(direction)
            positions[0] = newHead

            for (i in 1 until positions.size) {
                val partInFront = positions[i - 1]
                val part = positions[i]
                val followed = part.follow(partInFront)
                positions[i] = followed
            }
        }

        fun tailPos(): Position = positions.last()
    }

    fun Position.follow(other: Position): Position {
        fun Position.adjustX(other: Position): Int = if (x < other.x) x + 1 else x - 1
        fun Position.adjustY(other: Position): Int = if (y < other.y) y + 1 else y - 1

        val xDiff = abs(other.x - x)
        val yDiff = abs(other.y - y)
        var (newX, newY) = this

        if (xDiff == 2 && yDiff > 0 || yDiff == 2 && xDiff > 0) {
            newX = adjustX(other)
            newY = adjustY(other)
        } else if (xDiff == 2) {
            newX = adjustX(other)
        } else if (yDiff == 2) {
            newY = adjustY(other)
        }

        return Position(newX, newY)
    }

    data class Position(val x: Int, val y: Int) {
        fun move(direction: String): Position {
            return when (direction) {
                "R" -> Position(x + 1, y)
                "L" -> Position(x - 1, y)
                "U" -> Position(x, y + 1)
                "D" -> Position(x, y - 1)
                else -> throw IllegalArgumentException("unknown move $direction")
            }
        }
    }

    data class Step(
        val direction: String,
        val steps: Int,
    ) {
        companion object {
            fun of(s: String): Step = s.split(" ").let { (m, i) -> Step(m.first().toString(), i.toInt()) }
        }
    }
}
