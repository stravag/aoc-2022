import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03Test {

    @Test
    fun part1() {
        assertEquals(157, Day03.compute1(readInput("Day03_test")))
        println("part 1: " + Day03.compute1(readInput("Day03")))
    }

    @Test
    fun part2() {
        assertEquals(70, Day03.compute2(readInput("Day03_test")))
        println("part 2: " + Day03.compute2(readInput("Day03")))
    }
}