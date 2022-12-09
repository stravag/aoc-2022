import kotlin.math.abs

fun main() {

    /*
    execute(
        day = "Day09",
        part = Part(
            expectedTestResult = 13,
            expectedResult = 6212,
            compute = { compute(it, 2) }
        ),
    )
     */

    execute(
        day = "Day09",
        part = Part(
            expectedTestResult = 36,
            expectedResult = 2499,
            compute = { compute(it, 10) }
        ),
        partTestData = """
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20
        """.trimIndent().lines()
    )
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
    size: Int,
    val positions: MutableList<Position> = MutableList(size) { Position(0, 0) }
) {
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

private fun Position.follow(other: Position): Position {
    val xDiff = abs(other.x - x)
    val yDiff = abs(other.y - y)
    return if (xDiff > 1 || yDiff > 1) {
        this.putBehind(other)
    } else {
        this
    }
}

private fun Position.putBehind(other: Position): Position {
    val xDiff = other.x - x
    val yDiff = other.y - y
    return if (abs(xDiff) > abs(yDiff)) {
        // L/R
        if (xDiff > 0) {
            Position(other.x - 1, other.y)
        } else {
            Position(other.x + 1, other.y)
        }
    } else {
        // U/D
        if (yDiff > 0) {
            Position(other.x, other.y - 1)
        } else {
            Position(other.x, other.y + 1)
        }
    }
}

private data class Position(val x: Int, val y: Int) {
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
