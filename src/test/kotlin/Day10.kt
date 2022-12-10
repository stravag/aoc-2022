import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day10 : AbstractDay() {

    @Test
    fun tests() {
        assertEquals(146, compute1(testInput))
        assertEquals(136, compute1(puzzleInput))

        assertEquals(146, compute2(testInput))
        assertEquals(136, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input.size
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}
