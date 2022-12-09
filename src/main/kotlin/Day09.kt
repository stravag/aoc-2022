import kotlin.math.abs

fun main() {
    execute(
        day = "Day09",
        part1 = Part(
            expectedTestResult = 13,
            expectedResult = 6212,
            compute = ::compute1
        ),
        part2 = Part(
            expectedTestResult = 1,
            expectedResult = 0,
            compute = ::compute2
        )
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
        var lastMoved = true
        var i = 0
        while (lastMoved && i < positions.size - 1) {
            val (oldHead, oldTail) = positions.subList(i, i + 2)
            val (newHead, newTail) = (oldHead to oldTail).next(direction)
            lastMoved = oldTail != newTail
            positions[i] = newHead
            positions[i + 1] = newTail
            i++
        }
    }

    fun tailPos(): Position = positions.last()
}

private fun Pair<Position, Position>.next(direction: Char): Pair<Position, Position> {
    val (oldHead, oldTail) = this
    val newHead = oldHead.move(direction)
    val newTail = if (abs(newHead.x - oldTail.x) > 1 || abs(newHead.y - oldTail.y) > 1) {
        when (direction) {
            'R' -> {
                if (newHead.y == oldTail.y && newHead.x != oldTail.x) {
                    oldTail.move(direction)
                } else if (abs(newHead.x - oldTail.x) > 1) {
                    Position(newHead.x - 1, newHead.y)
                } else {
                    oldTail
                }
            }

            'L' -> {
                if (newHead.y == oldTail.y && newHead.x != oldTail.x) {
                    oldTail.move(direction)
                } else if (abs(newHead.x - oldTail.x) > 1) {
                    Position(newHead.x + 1, newHead.y)
                } else {
                    oldTail
                }
            }

            'U' -> {
                if (newHead.x == oldTail.x && newHead.y != oldTail.y) {
                    oldTail.move(direction)
                } else if (abs(newHead.y - oldTail.y) > 1) {
                    Position(newHead.x, newHead.y - 1)
                } else {
                    oldTail
                }
            }

            'D' -> {
                if (newHead.x == oldTail.x && newHead.y != oldTail.y) {
                    oldTail.move(direction)
                } else if (abs(newHead.y - oldTail.y) > 1) {
                    Position(newHead.x, newHead.y + 1)
                } else {
                    oldTail
                }
            }

            else -> throw IllegalArgumentException("unknown move $direction")
        }
    } else {
        oldTail
    }

    return newHead to newTail
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
