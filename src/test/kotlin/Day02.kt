import Day02.Outcome.*
import Day02.Shape.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day02 : AbstractDay() {
    @Test
    fun tests() {
        assertEquals(15, compute1(testInput))
        assertEquals(12586, compute1(puzzleInput))

        assertEquals(12, compute2(testInput))
        assertEquals(13193, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .map { it.parse() }
            .sumOf { (a, b) -> b.score + play(a, b).score }
    }

    private fun compute2(input: List<String>): Int {
        return input
            .map { it.parse2() }
            .sumOf { (a, o) -> determineAnswer(a, o).score + o.score }
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

    private enum class Outcome(val score: Int) {
        LOSE(0), DRAW(3), WIN(6);

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

    private fun play(a: Shape, b: Shape): Outcome {
        return when {
            (a == ROCK && b == SCISSOR) || (a == PAPER && b == ROCK) || (a == SCISSOR && b == PAPER) -> LOSE
            a == b -> DRAW
            else -> WIN
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
