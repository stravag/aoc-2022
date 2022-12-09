import kotlin.math.abs

private typealias P = Position

fun main() {

    // follow adjacent
    check(P(0, 0).follow(P(0, 0)) == P(0, 0))
    check(P(0, 0).follow(P(1, 1)) == P(0, 0))
    check(P(0, 0).follow(P(1, 0)) == P(0, 0))
    check(P(0, 0).follow(P(0, -1)) == P(0, 0))
    check(P(0, 0).follow(P(-1, -1)) == P(0, 0))
    check(P(0, 0).follow(P(-1, 0)) == P(0, 0))

    // follow right
    check(P(0, 0).follow(P(2, 0)) == P(1, 0))
    // follow left
    check(P(0, 0).follow(P(-2, 0)) == P(-1, 0))
    // follow up
    check(P(0, 0).follow(P(0, 2)) == P(0, 1))
    // follow down
    check(P(0, 0).follow(P(0, -2)) == P(0, -1))

    // follow up-right
    check(P(0, 0).follow(P(1, 2)) == P(1, 1))
    // follow up-left
    check(P(0, 0).follow(P(-1, 2)) == P(-1, 1))
    // follow down-right
    check(P(0, 0).follow(P(1, -2)) == P(1, -1))
    // follow down-left
    check(P(0, 0).follow(P(-1, -2)) == P(-1, -1))

    execute(
        day = "Day09",
        part = Part(
            expectedTestResult = 13,
            expectedResult = 6212,
            compute = { compute(it, 2) }
        ),
    )

    execute(
        day = "Day09",
        part = Part(
            expectedTestResult = 36,
            expectedResult = 2522,
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
    var newX = x
    var newY = y
    val xDiff = abs(o.x - x)
    val yDiff = abs(o.y - y)
    if ((xDiff > 1 && yDiff > 0) || xDiff > 0 && yDiff > 1) {
        if (o.x < x) newX -= 1 else newX += 1
        if (o.y < y) newY -= 1 else newY += 1
    } else if (xDiff > 1) {
        if (o.x < x) newX -= 1 else newX += 1
    } else if (yDiff > 1) {
        if (o.y < y) newY -= 1 else newY += 1
    }

    return Position(newX, newY)

    /*
    return if (xDiff > yDiff) {
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
     */
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
