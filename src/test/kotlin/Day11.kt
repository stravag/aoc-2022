import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day11 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(10605, compute(testInput))
        assertEquals(54036, compute(puzzleInput))
    }

    private fun compute(input: List<String>): Int {
        val monkeys = input.filter { it.isNotBlank() }
            .chunked(6)
            .map { Monkey.of(it) }

        val monkeyLookup = monkeys.mapIndexed { index, monkey -> index to monkey }.toMap()

        repeat(20) {
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    item.worryLevel = monkey.operation(item.worryLevel)
                    monkey.inspections++
                    item.worryLevel = item.worryLevel.floorDiv(3)
                    if (item.worryLevel % monkey.divisor == 0) {
                        monkeyLookup.getValue(monkey.trueMonkey).items.add(item)
                    } else {
                        monkeyLookup.getValue(monkey.falseMonkey).items.add(item)
                    }
                }
                monkey.items.clear()
            }
        }

        return monkeys
            .map { it.inspections }
            .sortedDescending()
            .take(2)
            .reduce(Int::times)
    }

    data class Item(
        var worryLevel: Int,
    )

    class Monkey(
        val items: MutableList<Item>,
        val operation: (Int) -> Int,
        val divisor: Int,
        val trueMonkey: Int,
        val falseMonkey: Int,
        var inspections: Int = 0,
    ) {

        companion object {
            fun of(lines: List<String>): Monkey {
                val items = extractItems(lines[1])
                val operation = extractOperation(lines[2])
                val divisor = extractDivisor(lines[3])
                val (trueMonkey, falseMonkey) = extractTargetMonkeys(lines[4], lines[5])
                return Monkey(items.toMutableList(), operation, divisor, trueMonkey, falseMonkey)
            }

            private fun extractItems(s: String): List<Item> {
                return s
                    .split(": ")[1]
                    .split(",")
                    .map { it.trim().toInt() }
                    .map { Item(it) }
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
