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
                val partInfront = positions[i - 1]
                val part = positions[i]
                val followed = part.follow(partInfront)
                positions[i] = followed
            }
        }

        fun tailPos(): Position = positions.last()
    }

    fun Position.follow(other: Position): Position {
        val xDiff = other.x - x
        val yDiff = other.y - y
        val isAdjacent = abs(xDiff) <= 1 && abs(yDiff) <= 1

        return if (isAdjacent) {
            this
        } else {
            this.putBehind(other)
        }
    }

    private fun Position.putBehind(o: Position): Position {
        val xDiff = abs(o.x - x)
        val yDiff = abs(o.y - y)

        var newX = x
        var newY = y
        if ((xDiff > 1 && yDiff > 0) || xDiff > 0 && yDiff > 1) {
            if (o.x < x) newX -= 1 else newX += 1
            if (o.y < y) newY -= 1 else newY += 1
        } else if (xDiff > 1) {
            if (o.x < x) newX -= 1 else newX += 1
        } else if (yDiff > 1) {
            if (o.y < y) newY -= 1 else newY += 1
        }

        val type1 = Position(newX, newY)


        val type2 = if (xDiff > yDiff) {
            if (o.x - x > 0) {
                // other more to the right of this: put on its left
                Position(o.x - 1, o.y)
            } else {
                // other more to the left of this: put on its right
                Position(o.x + 1, o.y)
            }
        } else {
            if (o.y - y > 0) {
                // other more to the top of this: put below it
                Position(o.x, o.y - 1)
            } else {
                // other more to the bottom of this: put above it
                Position(o.x, o.y + 1)
            }
        }

        if (type1 != type2) {
            println("WTF? Leader: $o / Follower: $this")
        }

        return type1
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
