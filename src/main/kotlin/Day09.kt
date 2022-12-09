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
            expectedTestResult = 0,
            expectedResult = 0,
            compute = ::compute2
        )
    )
}

private fun compute1(input: List<String>): Int {

    fun Pair<Position, Position>.next(direction: Char): Pair<Position, Position> {
        val (tailStart, headStart) = this
        var tailPos = tailStart
        var headPos = headStart

        if (tailPos == headPos) {
            // first step, tail stays same
            headPos = headPos.move(direction)
        } else {
            headPos = headPos.move(direction)
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

    val start = Position(0, 0)
    val visited = mutableSetOf(start)
    var last = start to start
    input
        .map { Step.of(it) }
        .flatMap { step -> List(step.steps) { step.direction } }
        .forEach { direction ->
            last = last.next(direction)
            visited.add(last.first)
        }

    return visited.size
}

private fun compute2(input: List<String>): Int {
    return 0
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
