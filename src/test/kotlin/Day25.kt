import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.pow
import kotlin.test.assertEquals

class Day25 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals("2=-1=0", compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals("2=2-1-010==-0-1-=--2", compute1(puzzleInput))
    }

    @Test
    fun toDecimalTests() {
        assertEquals(1747, "1=-0-2".toDecimal())
        assertEquals(906, "12111".toDecimal())
        assertEquals(198, "2=0=".toDecimal())
        assertEquals(201, "2=01".toDecimal())
    }

    @Test
    fun toSnafuTests() {
        assertEquals("1", 1L.toSNAFU())
        assertEquals("2", 2L.toSNAFU())
        assertEquals("1=", 3L.toSNAFU())
        assertEquals("1-", 4L.toSNAFU())
        assertEquals("10", 5L.toSNAFU())
        assertEquals("11", 6L.toSNAFU())
        assertEquals("12", 7L.toSNAFU())
        assertEquals("2=", 8L.toSNAFU())
        assertEquals("2-", 9L.toSNAFU())
        assertEquals("20", 10L.toSNAFU())
        assertEquals("1-0---0", 12345L.toSNAFU())
        assertEquals("1121-1110-1=0", 314159265L.toSNAFU())
    }

    private fun compute1(input: List<String>): String {
        return input
            .sumOf { it.toDecimal() }
            .toSNAFU()
    }

    private fun SNAFU.toDecimal(): Long {
        return this.reversed().mapIndexed { index, char ->
            val value = 5.0.pow(index).toLong()
            val number = when (char) {
                '2' -> 2
                '1' -> 1
                '0' -> 0
                '-' -> -1
                '=' -> -2
                else -> throw IllegalArgumentException("unexpected char $char")
            }
            number * value
        }.sum()
    }

    private fun Long.toSNAFU(): SNAFU {
        var numberToConvert = this
        var result = ""
        while (numberToConvert != 0L) {
            val (snafuDigit, carry) = when (numberToConvert % 5) {
                0L -> "0" to 0
                1L -> "1" to 0
                2L -> "2" to 0
                3L -> "=" to 1
                4L -> "-" to 1
                else -> throw RuntimeException()
            }
            numberToConvert = (numberToConvert / 5) + carry
            result = snafuDigit + result
        }
        return result
    }
}

typealias SNAFU = String
