import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day21 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(152, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(331319379445180, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val monkeys = parse(input)
        val rootMonkey = monkeys.getValue("root")

        return getAnswer(rootMonkey, monkeys)
    }

    private fun getAnswer(monkey: Monkey, monkeys: Map<String, Monkey>): Long {
        return when (monkey) {
            is NumberMonkey -> monkey.number
            is OperationMonkey -> {
                val a = getAnswer(monkeys.getValue(monkey.a), monkeys)
                val b = getAnswer(monkeys.getValue(monkey.b), monkeys)
                monkey.operation(a, b)
            }
        }
    }

    private fun compute2(input: List<String>): Long {
        TODO()
    }

    private fun parse(input: List<String>): Map<String, Monkey> {
        val monkeys = input.map {
            val (name, operationString) = it.split(": ")
            if (operationString.toLongOrNull() == null) {
                val (a, operator, b) = operationString.split(" ")
                val operation: (Long, Long) -> Long = when (operator) {
                    "+" -> Long::plus
                    "-" -> Long::minus
                    "*" -> Long::times
                    "/" -> Long::div
                    else -> throw IllegalArgumentException("unexpected operator $operator")
                }
                OperationMonkey(name, a, b, operation)
            } else {
                NumberMonkey(name, operationString.toLong())
            }
        }

        return monkeys.associateBy { it.name }
    }

    sealed interface Monkey {
        val name: String
    }

    data class NumberMonkey(
        override val name: String,
        val number: Long
    ) : Monkey

    data class OperationMonkey(
        override val name: String,
        val a: String,
        val b: String,
        val operation: (Long, Long) -> Long
    ) : Monkey
}
