import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day11 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(10605, part1(testInput))
        assertEquals(54036, part1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(2713310158, part2(testInput))
        assertEquals(13237873355, part2(puzzleInput))
    }

    private fun part1(input: List<String>): Long {
        return compute(parse(input), 20) { it.floorDiv(3) }
    }

    private fun part2(input: List<String>): Long {
        val monkeys = parse(input)
        val lcm = monkeys.map { it.divisor }.fold(1L) { acc, i -> acc * i }
        return compute(parse(input), 10000) { it % lcm }
    }

    private fun compute(monkeys: List<Monkey>, rounds: Int, adjustment: Adjustment): Long {
        repeat(rounds) {
            monkeys.forEach { monkey ->
                monkey.items.forEach { item ->
                    monkey.inspections++
                    item.worryLevel = adjustment(monkey.operation(item.worryLevel))
                    if (item.worryLevel % monkey.divisor == 0L) {
                        monkeys[monkey.trueMonkey].items.add(item)
                    } else {
                        monkeys[monkey.falseMonkey].items.add(item)
                    }
                }
                monkey.items.clear()
            }
        }

        return monkeys.map { it.inspections }.sortedDescending().take(2).reduce(Long::times)
    }

    private fun parse(input: List<String>): List<Monkey> {
        return input.filter { it.isNotBlank() }.chunked(6).map { Monkey.of(it) }
    }

    data class Item(
        var worryLevel: Long,
    )

    class Monkey(
        val items: MutableList<Item>,
        val operation: (Long) -> Long,
        val divisor: Long,
        val trueMonkey: Int,
        val falseMonkey: Int,
        var inspections: Long = 0,
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
                return s.split(": ")[1].split(",").map { it.trim().toLong() }.map { Item(it) }
            }

            private fun extractOperation(s: String): (Long) -> Long {
                val opStr = s.split(" = ")[1].split(" ")
                val a = opStr[0].toLongOrNull()
                val b = opStr[2].toLongOrNull()
                val operation: (Long, Long) -> Long = when (opStr[1]) {
                    "*" -> Long::times
                    "+" -> Long::plus
                    else -> throw IllegalArgumentException()
                }
                return {
                    val opA = a ?: it
                    val opB = b ?: it
                    operation(opA, opB)
                }
            }

            private fun extractDivisor(s: String): Long {
                return s.split(" ").last().toLong()
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

private typealias Adjustment = (Long) -> Long
