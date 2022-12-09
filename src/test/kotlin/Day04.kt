import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day04 : AbstractDay() {
    @Test
    fun tests() {
        assertEquals(2, compute1(testInput))
        assertEquals(487, compute1(puzzleInput))

        assertEquals(4, compute2(testInput))
        assertEquals(849, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int = input.parse()
        .count { (a, b) -> a.contains(b) || b.contains(a) }

    private fun compute2(input: List<String>): Int = input.parse()
        .count { (a, b) -> a.overlaps(b) || b.overlaps(a) }

    private fun List<String>.parse(): List<Pair<IntRange, IntRange>> = this
        .map { it.split(",") }
        .map { (a, b) -> Pair(a.toIntRange(), b.toIntRange()) }

    private fun String.toIntRange(): IntRange = this
        .split("-")
        .map { it.toInt() }
        .let { (a, b) -> a..b }

    private fun IntRange.contains(other: IntRange): Boolean {
        return contains(other.first) && contains(other.last)
    }

    private fun IntRange.overlaps(other: IntRange): Boolean {
        return contains(other.first) || contains(other.last)
    }
}
