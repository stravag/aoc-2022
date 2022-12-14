import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

object Day13 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(13, compute1(testInput))
        //assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(23, compute2(testInput))
        assertEquals(449, compute2(puzzleInput))
    }

    @Test
    fun testCompare() {
        assertEquals(
            0, compute1(
                listOf(
                    "[7,7,7,7]",
                    "[7,7,7]",
                )
            )
        )

        assertEquals(
            0, compute1(
                listOf(
                    "[9]",
                    "[[8,7,6]]",
                )
            )
        )
    }

    private fun compute1(input: List<String>): Int {
        val debugVar = input
            .filter { line -> line.isNotEmpty() }
            .chunked(2) { lines ->
                val (left, right) = lines.map { parse(it) }
                left <= right
            }.mapIndexed { index, right -> index + 1 to right }

        return debugVar
            .filter { it.second } // filter pairs in right order
            .sumOf { it.first }
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

        private fun getOrNull(idx: Int) = packets.getOrNull(idx)

        private fun isNumber(): Boolean = int != null
        private fun size(): Int = packets.size

        override fun compareTo(other: Packet): Int {
            val size = max(this.size(), other.size())
            for (i in 0 until size) {
                val nextLeft = this.getOrNull(i)
                val nextRight = other.getOrNull(i)
                val c = if (nextLeft != null && nextRight != null) {
                    if (nextLeft.isNumber() && nextRight.isNumber()) {
                        nextLeft.int!!.compareTo(nextRight.int!!)
                    } else {
                        val l = if (nextLeft.isNumber()) p(nextLeft) else nextLeft
                        val r = if (nextRight.isNumber()) p(nextRight) else nextRight
                        l.compareTo(r)
                    }
                } else if (nextLeft == null) {
                    return -1 // left ran out of items
                } else {
                    return 1 // right ran out of items
                }
                if (c != 0) return c // continue only if same
            }
            return -1 // if we made it this far it's fine
        }

        companion object {
            fun p(p: Packet) = Packet(null, mutableListOf(p))
        }
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}
