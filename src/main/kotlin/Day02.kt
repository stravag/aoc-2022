import Day02.Shape.*

object Day02 {
    fun compute(input: List<String>): Int {
        return input
            .map { it.parse() }
            .map { (a, b) -> b.score + play(a, b) }
            .sum()
    }

    private enum class Shape( val score: Int) {
        ROCK(1),
        PAPER(2),
        SCISSOR(3);

        companion object {
            fun of(s: String): Shape {
                return when (s) {
                    "A", "X" -> ROCK
                    "B", "Y" -> PAPER
                    "C", "Z" -> SCISSOR
                    else -> throw IllegalArgumentException(s)
                }
            }
        }
    }

    private fun String.parse(): Pair<Shape, Shape> {
        val (a, b) = this.split(" ")
            .map { Shape.of(it) }

        return a to b
    }

    private fun play(a: Shape, b: Shape): Int {
        return when {
            (a == ROCK && b == SCISSOR) || (a == PAPER && b == ROCK) || (a == SCISSOR && b == PAPER) -> 0 // lose
            a == b -> 3 // draw
            else -> 6 // win
        }
    }
}
