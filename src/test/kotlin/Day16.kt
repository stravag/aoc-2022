import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day16 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(1, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(1, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return parse(input).hashCode()
    }

    private fun compute2(input: List<String>): Int {
        return parse(input).hashCode()
    }

    private fun parse(input: List<String>): Any {
        return input
    }
}
