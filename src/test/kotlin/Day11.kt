import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day11 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(27, compute(testInput))
    }

    private fun compute(input: List<String>): Int {
        val monkeys = input.filter { it.isNotBlank() }
            .chunked(6)
            .map {
                Monkey.of(it)
            }
        return input.size
    }

    class Monkey {
        companion object {
            fun of(lines: List<String>): Monkey {
                return Monkey()
            }
        }
    }
}
