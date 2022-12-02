import Day02.Outcome.*
import Day02.Shape.*

object Day02 {
    fun compute(input: List<String>): Int {
        return input
            .map { it.parse() }
            .sumOf { (a, b) -> b.score + play(a, b) }
    }

    fun compute2(input: List<String>): Int {
        return input
            .map { it.parse2() }
            .map { (a, o) -> a to determineAnswer(a, o) }
            .sumOf { (a, b) -> b.score + play(a, b)}
    }

    private enum class Shape(val score: Int) {
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

    private enum class Outcome {
        LOSE, DRAW, WIN;

        companion object {
            fun of(s: String): Outcome {
                return when (s) {
                    "X" -> LOSE
                    "Y" -> DRAW
                    "Z" -> WIN
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

    private fun String.parse2(): Pair<Shape, Outcome> {
        val (shapeStr, outcomeStr) = this.split(" ")

        return Shape.of(shapeStr) to Outcome.of(outcomeStr)
    }

    private fun play(a: Shape, b: Shape): Int {
        return when {
            (a == ROCK && b == SCISSOR) || (a == PAPER && b == ROCK) || (a == SCISSOR && b == PAPER) -> 0 // lose
            a == b -> 3 // draw
            else -> 6 // win
        }
    }

    private fun determineAnswer(a: Shape, o: Outcome): Shape {
        return when (o) {
            DRAW -> a
            LOSE -> when (a) {
                ROCK -> SCISSOR
                PAPER -> ROCK
                SCISSOR -> PAPER
            }
            WIN -> when (a) {
                ROCK -> PAPER
                PAPER -> SCISSOR
                SCISSOR -> ROCK
            }
        }
    }
}
