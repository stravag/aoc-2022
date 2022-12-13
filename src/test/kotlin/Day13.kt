import org.junit.jupiter.api.Test
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
    fun test() {
        compute1(listOf("[[1],[2]]"))
    }

    private fun compute1(input: List<String>): Int {
        val res = input
            .filter { it.isNotEmpty() }
            .map { parse(it) }
        return input.size
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
        val i: Int? = null,
        val packets: MutableList<Packet> = mutableListOf()
    ) {
        fun add(i: Int) {
            packets.add(Packet(i))
        }

        fun add(p: Packet) {
            packets.add(p)
        }
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}
