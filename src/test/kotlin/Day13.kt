import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

object Day13 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(0, compute1(testInput))
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    @Test
    fun testCompare() {
        compute1(listOf("[[1],[2]]"))
    }

    private fun compute1(input: List<String>): Int {
        input
            .filter { line -> line.isNotEmpty() }
            .chunked(2) { lines ->
                val (left, right) = lines.map { parse(it) }

            }
    }

    private fun parse(line: String): Packet {
        fun gatherPackets(line: String, idxOffset: Int, parentPacket: Packet): Int {
            var idx = idxOffset
            while (idx <= line.length) {
                val c = line[idx]
                when (c) {
                    '[' -> {
                        val packet = Packet()
                        parentPacket.add(packet)
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
                        parentPacket.add(numStr.toInt())
                        idx++
                    }
                }
            }
            return idx + 1
        }

        val packet = Packet()
        gatherPackets(line, 1, packet)
        return packet
    }

    data class Packet(
        val int: Int? = null,
        val packets: MutableList<Packet> = mutableListOf()
    ) : Comparable<Packet> {
        fun add(i: Int) {
            packets.add(Packet(i))
        }

        fun add(p: Packet) {
            packets.add(p)
        }

        override fun compareTo(other: Packet): Int {
            if (this.int != null && other.int != null) return int.compareTo(other.int)
            val size = max(this.packets.size, other.packets.size)
            for (i in 0 until size) {
                val left = this.packets.getOrNull(i) ?: this.int
                val right = other.packets.getOrNull(i) ?: other.int
                if (left == null && right != null) {
                    return -1 // left ran out of items
                }
                if (left != null && right != null) {
                    if (left is )
                }
            }
        }
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}
