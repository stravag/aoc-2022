import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day05Test {

    @Test
    fun part1() {
        assertEquals("CMZ", Day05.compute1(readInput("Day05_test")))
        println("part 1: " + Day05.compute1(readInput("Day05")))
    }
}