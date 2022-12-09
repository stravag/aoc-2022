import kotlin.math.abs

fun main() {
    execute(
        day = "Day09",
        part = Part(
            expectedTestResult = 13,
            expectedResult = 6212,
            compute = ::compute1
        ),
    )

    execute(
        day = "Day09",
        part = Part(
            expectedTestResult = 1,
            expectedResult = 0,
            compute = ::compute2
        ),
    )
}

private fun compute1(input: List<String>): Int {
    val rope = Rope(2)
    val visited = mutableSetOf(rope.tailPos())
    input
        .map { Step.of(it) }
        .flatMap { step -> List(step.steps) { step.direction } }
        .forEach { direction ->
            rope.move(direction)
            visited.add(rope.tailPos())
        }

    return visited.size
}

private fun compute2(input: List<String>): Int {
    val rope = Rope(10)
    val visited = mutableSetOf(rope.tailPos())
    input
        .map { Step.of(it) }
        .flatMap { step -> List(step.steps) { step.direction } }
        .forEach { direction ->
            rope.move(direction)
            visited.add(rope.tailPos())
        }

    return visited.size
}


private class Rope(
    size: Int,
    val positions: MutableList<Position> = MutableList(size) { Position(0, 0) }
) {
    fun move(direction: Char) {
        val newHead = positions.first().move(direction)
        positions[0] = newHead

        for (i in 1 until positions.size) {
            val partInfront = positions[i - 1]
            val part = positions[i]
            if (!areAdjacent(part, partInfront)) {
                positions[i] = part.follow(partInfront, direction)
            } else {
                break
            }
        }
    }

    fun tailPos(): Position = positions.last()
}

private fun areAdjacent(pos1: Position, pos2: Position): Boolean {
    return abs(pos1.x - pos2.x) <= 1 && abs(pos1.y - pos2.y) <= 1
}

private fun Position.follow(other: Position, direction: Char): Position {
    return if (areAdjacent(this, other)) {
        this
    } else {
        when (direction) {
            'R' -> {
                if (other.y == y && other.x != x) {
                    move(direction)
                } else if (abs(other.x - x) > 1) {
                    Position(other.x - 1, other.y)
                } else {
                    this
                }
            }

            'L' -> {
                if (other.y == y && other.x != x) {
                    move(direction)
                } else if (abs(other.x - x) > 1) {
                    Position(other.x + 1, other.y)
                } else {
                    this
                }
            }

            'U' -> {
                if (other.x == x && other.y != y) {
                    move(direction)
                } else if (abs(other.y - y) > 1) {
                    Position(other.x, other.y - 1)
                } else {
                    this
                }
            }

            'D' -> {
                if (other.x == x && other.y != y) {
                    move(direction)
                } else if (abs(other.y - y) > 1) {
                    Position(other.x, other.y + 1)
                } else {
                    this
                }
            }

            else -> throw IllegalArgumentException("unknown move $direction")
        }
    }
}

private data class Position(val x: Int, val y: Int) {
    fun move(direction: Char): Position {
        return when (direction) {
            'R' -> Position(x + 1, y)
            'L' -> Position(x - 1, y)
            'U' -> Position(x, y + 1)
            'D' -> Position(x, y - 1)
            else -> throw IllegalArgumentException("unknown move $direction")
        }
    }
}

data class Step(
    val direction: Char,
    val steps: Int,
) {
    companion object {
        fun of(s: String): Step = s.split(" ").let { (m, i) -> Step(m.first(), i.toInt()) }
    }
}
