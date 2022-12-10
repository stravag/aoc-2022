import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day10 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(13140, compute(testInput))
    }

    @Test
    fun part2() {
        compute(puzzleInput)
    }

    private fun compute(input: List<String>): Int {
        val cpu = CPU()
        input.forEach {
            cpu.exec(it)
        }

        return cpu.currentSignal()
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
            this.print()
            if (CHECK.contains(cycleCnt)) {
                signal += cycleCnt * register
            }
        }

        private fun print() {
            val sprite = Sprite(register)
            val pixel = sprite.pixel(cycleCnt - 1)
            print(pixel)
            if (cycleCnt % 40 == 0) {
                println()
            }
        }

        private data class Sprite(val sprite: List<Char>) {
            constructor(register: Int) : this(
                List(40) {
                    if (it in IntRange(register - 1, register + 1)) '#' else ' '
                }
            )

            fun pixel(cycleCnt: Int): Char = sprite[cycleCnt % 40]

            override fun toString(): String = sprite.joinToString(separator = "") { it.toString() }
        }

        companion object {
            private val CHECK = listOf(20, 60, 100, 140, 180, 220)
        }
    }
}
