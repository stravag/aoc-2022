import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day01Test {

    @Test
    fun part1() {
        assertEquals(24000, Day01.part1(readInput("Day01_test")))
        println(Day01.part1(readInput("Day01")))
    }

    @Test
    fun part2() {
        assertEquals(45000, Day01.part2(readInput("Day01_test")))
        println(Day01.part1(readInput("Day01")))
    }
}