import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03Test {

    @Test
    fun part1() {
        assertEquals(157, Day03.compute(readInput("Day03_test")))
        println("part 1: " + Day03.compute(readInput("Day03")))
    }
}