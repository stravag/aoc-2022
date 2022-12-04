import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day04Test {

    @Test
    fun part1() {
        assertEquals(2, Day04.compute1(readInput("Day04_test")))
        println("part 1: " + Day04.compute1(readInput("Day04")))
    }

    @Test
    fun part2() {
        assertEquals(4, Day04.compute2(readInput("Day04_test")))
        println("part 2: " + Day04.compute2(readInput("Day04")))
    }
}