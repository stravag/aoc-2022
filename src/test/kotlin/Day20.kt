import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

class Day20 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(3, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(11123, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(1623178306, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(4248669215955, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val sequence = parse(input)
        sequence.mix()

        return listOf(1000, 2000, 3000).sumOf { sequence.getAfterZero(it) }
    }

    private fun compute2(input: List<String>): Long {
        val sequence = parse(input)

        val decryptionKey = 811589153L
        repeat(10) {
            sequence.mix(decryptionKey)
        }

        return listOf(1000, 2000, 3000).sumOf { sequence.getAfterZero(it) * decryptionKey }
    }

    private fun parse(input: List<String>): Sequence {
        return input.map { it.toInt() }.let { Sequence.build(it) }
    }

    private data class Sequence(
        val sequence: MutableList<Element>,
        val unprocessed: List<Element>,
    ) {
        fun mix(decryptionKey: Long = 1) {
            unprocessed.forEach { elementToMix ->
                val index = sequence.indexOf(elementToMix)
                mix(index, decryptionKey)
            }
        }

        private fun mix(index: Int, decryptionKey: Long = 1) {
            val endIndex = sequence.size - 1

            val elementToMix = sequence[index]
            val numberToMix = (elementToMix.number * decryptionKey % endIndex).toInt()

            val newIndex = if (numberToMix < 0 && abs(numberToMix) >= index) {
                // wrap around left
                endIndex - abs(index - abs(numberToMix))
            } else if (numberToMix > 0 && (index + numberToMix) >= endIndex) {
                // wrap around right
                index + numberToMix - endIndex
            } else {
                index + numberToMix
            }

            sequence.removeAt(index)
            sequence.add(newIndex, elementToMix)
        }

        fun getAfterZero(n: Int): Int {
            val zeroIndex = sequence.indexOfFirst { it.number == 0 }
            val index = (zeroIndex + n) % sequence.size
            return sequence[index].number
        }

        companion object {
            fun build(numbers: List<Int>): Sequence {
                val nodes = numbers.mapIndexed { index, i ->
                    Element(i, index)
                }
                return Sequence(
                    sequence = ArrayList(nodes),
                    unprocessed = ArrayList(nodes),
                )
            }
        }
    }

    private data class Element(
        val number: Int,
        private val id: Int, // ensures uniqueness
    )
}
