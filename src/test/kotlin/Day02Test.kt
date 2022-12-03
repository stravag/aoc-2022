import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02Test {

    @Test
    fun part1() {
        assertEquals(15, Day02.compute(readInput("Day02_test")))
        println("part 1: " + Day02.compute(readInput("Day02")))
    }

    @Test
    fun part2() {
        assertEquals(12, Day02.compute2(readInput("Day02_test")))
        println("part 2: " + Day02.compute2(readInput("Day02")))
    }
}