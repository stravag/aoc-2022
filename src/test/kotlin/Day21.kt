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
        assertEquals(301, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(3715799488132, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val monkeys = parse1(input)
        return getAnswer("root", monkeys)
    }

    private fun getAnswer(monkeyName: String, monkeys: Map<String, Monkey>): Long {
        return when (val monkey = monkeys.getValue(monkeyName)) {
            is NumberMonkey -> monkey.number
            is OperationMonkey -> {
                val a = getAnswer(monkey.a, monkeys)
                val b = getAnswer(monkey.b, monkeys)
                monkey.operation(a, b)
            }
        }
    }

    private fun compute2(input: List<String>): Long {
        val (monkeys, monkeyParents) = parse2(input)

        // bubble up
        val operationWithHumn = monkeyParents.getValue("humn")
        val operationStack = mutableListOf("humn" to operationWithHumn)
        var lastOperation = operationWithHumn
        while (lastOperation.name != "root") {
            lastOperation = monkeyParents.getValue(lastOperation.name)
            operationStack.add(operationStack.last().second.name to lastOperation)
        }

        // bubble down
        val (unknownRootPart, rootOperation) = operationStack.removeLast()
        val equalityCheck = rootOperation.getKnownPart(unknownRootPart, monkeys)
        return operationStack.reversed().fold(equalityCheck) { desiredAnswer, (unknownPart, operation) ->
            operation.getUnknownPart(desiredAnswer, unknownPart, monkeys)
        }
    }

    private fun OperationMonkey.getKnownPart(unknownPart: String, monkeys: Map<String, Monkey>): Long {
        return when {
            a == unknownPart -> getAnswer(b, monkeys)
            b == unknownPart -> getAnswer(a, monkeys)
            else -> throw IllegalArgumentException()
        }
    }

    private fun OperationMonkey.getUnknownPart(
        desiredAnswer: Long,
        unknownPart: String,
        monkeys: Map<String, Monkey>
    ): Long {
        val knownPart = getKnownPart(unknownPart, monkeys)
        return when (operator) {
            '+' -> desiredAnswer - knownPart
            '-' -> {
                if (a == unknownPart) {
                    desiredAnswer + knownPart
                } else {
                    knownPart - desiredAnswer
                }
            }

            '*' -> desiredAnswer / getKnownPart(unknownPart, monkeys)
            '/' -> {
                if (a == unknownPart) {
                    desiredAnswer * knownPart
                } else {
                    knownPart / desiredAnswer
                }
            }

            else -> throw IllegalArgumentException("unexpected operator $operator")
        }
    }

    private fun parse1(input: List<String>): Map<String, Monkey> {
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
                OperationMonkey(name, a, b, operator[0], operation)
            } else {
                NumberMonkey(name, operationString.toLong())
            }
        }

        return monkeys.associateBy { it.name }
    }

    private fun parse2(input: List<String>): Pair<Map<String, Monkey>, Map<String, OperationMonkey>> {
        val lookup = parse1(input)
        val parentLookup = lookup.values
            .filterIsInstance<OperationMonkey>()
            .flatMap {
                listOf(
                    it.a to it,
                    it.b to it,
                )
            }.toMap()
        return lookup to parentLookup
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
        val operator: Char,
        val operation: (Long, Long) -> Long,
    ) : Monkey
}
