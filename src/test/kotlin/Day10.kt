import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day10 : AbstractDay() {

    @Test
    fun tests() {
        assertEquals(13140, compute1(testInput))
        assertEquals(16880, compute1(puzzleInput))

        assertEquals(146, compute2(testInput))
        assertEquals(136, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val cpu = CPU()
        input.forEach {
            cpu.exec(it)
        }

        return cpu.currentSignal()
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    class CPU(
        private var register: Int = 1,
        private var cycleCnt: Int = 0,
        private var signal: Int = 0,
    ) {
        fun exec(cmd: String) {
            val parts = cmd.split(" ")
            when (parts.first()) {
                "noop" -> noop()
                "addx" -> add(parts[1].toInt())
            }
        }

        fun currentSignal(): Int = signal

        private fun noop() {
            tick()
        }

        private fun add(i: Int) {
            tick()
            tick()
            register += i
        }

        private fun tick() {
            cycleCnt++
            if (CHECK.contains(cycleCnt)) {
                signal += cycleCnt * register
            }
        }

        companion object {
            private val CHECK = listOf(20, 60, 100, 140, 180, 220)
        }
    }
}
