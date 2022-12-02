import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02Test {

    @Test
    fun part1() {
        assertEquals(15, Day02.compute(readInput("Day01_test")))
        println(Day01.part1(readInput("Day01")))
    }
}