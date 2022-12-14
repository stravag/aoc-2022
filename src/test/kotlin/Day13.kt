import org.junit.jupiter.api.Test
import kotlin.math.min
import kotlin.test.assertEquals

object Day13 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(13, compute1(testInput))
        assertEquals(5806, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(140, compute2(testInput))
        assertEquals(23600, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return parse(input)
            .chunked(2) { (l, r) -> l <= r }
            .mapIndexed { idx, right -> idx + 1 to right }
            .filter { it.second } // filter pairs in right order
            .sumOf { it.first }
    }

    private fun compute2(input: List<String>): Int {
        val divider1 = parse("[[2]]")
        val divider2 = parse("[[6]]")
        val dividerIdx = listOf(divider1, divider2)
            .plus(parse(input))
            .sorted()
            .mapIndexedNotNull { index, listPacket ->
                if (listPacket == divider1 || listPacket == divider2) {
                    index + 1
                } else {
                    null
                }
            }

        return dividerIdx[0] * dividerIdx[1]
    }

    private fun parse(lines: List<String>): List<ListPacket> {
        return lines.filter { it.isNotEmpty() }.map { parse(it) }
    }

    private fun parse(line: String): ListPacket {
        fun gatherPackets(line: String, idxOffset: Int, enclosing: ListPacket): Int {
            var idx = idxOffset
            while (idx <= line.length) {
                when (line[idx]) {
                    '[' -> {
                        val packet = ListPacket()
                        enclosing.add(packet)
                        idx = gatherPackets(line, idx + 1, packet)
                    }

                    ']' -> {
                        return idx + 1
                    }

                    ',' -> {
                        idx++
                    }

                    else -> {
                        val numStr = line.substring(idx)
                            .takeWhile { it.isDigit() }
                        enclosing.add(IntPacket(numStr.toInt()))
                        idx += numStr.length
                    }
                }
            }
            return idx + 1
        }

        val packet = ListPacket()
        gatherPackets(line, 1, packet)
        return packet
    }

    sealed interface Packet

    data class IntPacket(private val int: Int) : Packet, Comparable<IntPacket> {
        override fun compareTo(other: IntPacket): Int {
            return int.compareTo(other.int)
        }
    }

    data class ListPacket(
        private val list: MutableList<Packet> = mutableListOf()
    ) : Packet, Comparable<ListPacket> {

        constructor(p: IntPacket) : this(mutableListOf(p))

        fun add(p: Packet) = list.add(p)

        override fun compareTo(other: ListPacket): Int {
            val s = min(list.size, other.list.size)
            for (i in 0 until s) {
                val left = this.list[i]
                val right = other.list[i]
                if (left is IntPacket && right is IntPacket) {
                    if (left != right) {
                        return left.compareTo(right)
                    }
                } else {
                    val l = if (left is IntPacket) ListPacket(left) else left as ListPacket
                    val r = if (right is IntPacket) ListPacket(right) else right as ListPacket
                    if (l != r) {
                        return l.compareTo(r)
                    }
                }
            }
            return list.size - other.list.size
        }
    }

}
