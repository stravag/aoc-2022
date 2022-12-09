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
            val window = positions.subList(i, i + 2).toPair()
            val oldTail = window.first
            val (newTail, newHead) = window.next(direction)
            lastMoved = oldTail != newTail
            positions[i] = newHead
            positions[i+1] = newTail
            i++
        }
    }

    fun List<Position>.toPair() = this[1] to this[0]

    fun tailPos(): Position = positions.last()
}

private fun Pair<Position, Position>.next(direction: Char): Pair<Position, Position> {
    val (tailStart, headStart) = this
    var tailPos = tailStart
    var headPos = headStart

    headPos = headPos.move(direction)
    // tail needs to follow
    if (abs(headPos.x - tailPos.x) > 1 || abs(headPos.y - tailPos.y) > 1) {
        tailPos = when (direction) {
            'R' -> {
                if (headPos.y == tailPos.y && headPos.x != tailPos.x) {
                    tailPos.move(direction)
                } else if (abs(headPos.x - tailPos.x) > 1) {
                    Position(headPos.x - 1, headPos.y)
                } else {
                    tailPos
                }
            }

            'L' -> {
                if (headPos.y == tailPos.y && headPos.x != tailPos.x) {
                    tailPos.move(direction)
                } else if (abs(headPos.x - tailPos.x) > 1) {
                    Position(headPos.x + 1, headPos.y)
                } else {
                    tailPos
                }
            }

            'U' -> {
                if (headPos.x == tailPos.x && headPos.y != tailPos.y) {
                    tailPos.move(direction)
                } else if (abs(headPos.y - tailPos.y) > 1) {
                    Position(headPos.x, headPos.y - 1)
                } else {
                    tailPos
                }
            }

            'D' -> {
                if (headPos.x == tailPos.x && headPos.y != tailPos.y) {
                    tailPos.move(direction)
                } else if (abs(headPos.y - tailPos.y) > 1) {
                    Position(headPos.x, headPos.y + 1)
                } else {
                    tailPos
                }
            }

            else -> throw IllegalArgumentException("unknown move $direction")
        }
    }

    return tailPos to headPos
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
