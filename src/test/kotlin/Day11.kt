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

    class Monkey(
        val items: List<Int>,
        val operation: (Int) -> Int,
        val divisor: Int,
        val trueMonkey: Int,
        val falseMonkey: Int
    ) {

        companion object {
            fun of(lines: List<String>): Monkey {
                val items = extractItems(lines[1])
                val operation = extractOperation(lines[2])
                val divisor = extractDivisor(lines[3])
                val (trueMonkey, falseMonkey) = extractTargetMonkeys(lines[4], lines[5])
                return Monkey(items, operation, divisor, trueMonkey, falseMonkey)
            }

            private fun extractItems(s: String): List<Int> {
                return s
                    .split(": ")[1]
                    .split(",")
                    .map { it.trim().toInt() }
            }

            private fun extractOperation(s: String): (Int) -> Int {
                val opStr = s.split(" = ")[1].split(" ")
                val a = opStr[0].toIntOrNull()
                val b = opStr[2].toIntOrNull()
                val operation: (Int, Int) -> Int = when (opStr[1]) {
                    "*" -> Int::times
                    "+" -> Int::plus
                    else -> throw IllegalArgumentException()
                }
                return {
                    val opA = a ?: it
                    val opB = b ?: it
                    operation(opA, opB)
                }
            }

            private fun extractDivisor(s: String): Int {
                return s.split(" ").last().toInt()
            }

            private fun extractTargetMonkeys(trueMonkey: String, falseMonkey: String): Pair<Int, Int> {
                return Pair(
                    trueMonkey.split(" ").last().toInt(),
                    falseMonkey.split(" ").last().toInt(),
                )
            }
        }
    }
}
