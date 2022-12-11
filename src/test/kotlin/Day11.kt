import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day11 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(27, compute(testInput))
    }

    private fun compute(input: List<String>): Int {
        return input.size
    }

}
