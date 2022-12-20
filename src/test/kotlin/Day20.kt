import org.junit.jupiter.api.Test
import java.util.*
import kotlin.collections.ArrayList
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

    @Test
    fun tests() {
        fun mix(index: Int, vararg sequence: Int): List<Int> {
            val s = Sequence.build(*sequence)
            s.mix(index)
            return s.getNumbers()
        }

        assertEquals(listOf(0, 6, 88, 99), mix(index = 1, 0, 6, 88, 99))
        assertEquals(listOf(0, 88, 1, 99), mix(index = 1, 0, 1, 88, 99))
        assertEquals(listOf(0, 3, 88, 99), mix(index = 1, 0, 3, 88, 99))
    }

    private fun compute1(input: List<String>): Int {
        val sequence = parse(input)

        while (sequence.hasNext()) {
            sequence.mix()
        }

        return listOf(1000, 2000, 3000).sumOf { sequence.getAfterZero(it) }
    }

    private fun compute2(input: List<String>): Long {
        val sequence = parse(input)

        val decryptionKey = 811589153L
        repeat(10) {
            while (sequence.hasNext()) {
                sequence.mix(decryptionKey)
            }
            sequence.reset()
        }

        return listOf(1000, 2000, 3000).sumOf { sequence.getAfterZero(it) * decryptionKey }
    }

    private fun parse(input: List<String>): Sequence {
        return input.map { it.toInt() }.let { Sequence.build(it) }
    }

    data class Sequence(
        val sequence: LinkedList<Element>,
        val unprocessed: LinkedList<Element>,
        val zeroElement: Element,
    ) {
        private val originalSequence: List<Element> = ArrayList(unprocessed)

        fun reset() {
            unprocessed.clear()
            unprocessed.addAll(originalSequence)
        }

        fun hasNext(): Boolean = unprocessed.isNotEmpty()

        fun mix(decryptionKey: Long = 1) {
            val elementToMix = unprocessed.removeFirst()
            val index = sequence.indexOf(elementToMix)
            mix(index, decryptionKey)
        }

        fun mix(index: Int, decryptionKey: Long = 1) {
            val endIndex = sequence.size - 1

            val elementToMix = sequence[index]
            val numberToMix = ((elementToMix.int * decryptionKey) % endIndex).toInt()

            val newIndex = if (numberToMix < 0 && abs(numberToMix) >= index) {
                // wrap around left
                endIndex - abs(index - abs(numberToMix))
            } else if (numberToMix > 0 && (index + numberToMix) >= endIndex) {
                // wrap around right
                0 + index + numberToMix - endIndex
            } else {
                index + numberToMix
            }

            sequence.removeAt(index)
            sequence.add(newIndex, elementToMix)
        }

        fun getAfterZero(n: Int): Int {
            val zeroIndex = sequence.indexOf(zeroElement)
            val index = (zeroIndex + n) % sequence.size
            return sequence[index].int
        }

        fun getNumbers(): List<Int> {
            return sequence.map { it.int }
        }

        override fun toString(): String {
            return sequence.joinToString { it.int.toString() }
        }

        companion object {
            fun build(vararg numbers: Int): Sequence {
                return build(numbers.toList())
            }

            fun build(numbers: List<Int>): Sequence {
                var zeroElement: Element? = null
                val nodes = numbers.map {
                    val element = Element(it)
                    if (element.int == 0) zeroElement = element
                    element
                }
                return Sequence(
                    sequence = LinkedList(nodes),
                    unprocessed = LinkedList(nodes),
                    zeroElement = zeroElement!!
                )
            }
        }
    }

    data class Element(
        val int: Int,
        val id: UUID = UUID.randomUUID()
    )
}
