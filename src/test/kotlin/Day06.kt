import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day06: AbstractDay() {
    @Test
    fun tests() {

        assertEquals(
            listOf(5, 6, 10, 11), compute1(
                listOf(
                    "bvwbjplbgvbhsrlpgdmjqwftvncz",
                    "nppdvjthqldpwncqszvftbrmjlhg",
                    "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg",
                    "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw",
                )
            )
        )
        assertEquals(listOf(1080), compute1(puzzleInput))

        assertEquals(
            listOf(19, 23, 23, 29, 26), compute2(
                listOf(
                    "mjqjpqmgbljsphdztnvjfqwrcgsmlb",
                    "bvwbjplbgvbhsrlpgdmjqwftvncz",
                    "nppdvjthqldpwncqszvftbrmjlhg",
                    "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg",
                    "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw",
                )
            )
        )
        assertEquals(listOf(3645), compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): List<Int> {
        return input.map { it.findSignalLoc(4) }
    }

    private fun compute2(input: List<String>): List<Int> {
        return input.map { it.findSignalLoc(14) }
    }

    private fun String.findSignalLoc(signalSize: Int): Int {
        for (i in signalSize..length) {
            val chunk = subSequence(i - signalSize, i)
            if (chunk.toSet().size == signalSize) return i
        }
        throw IllegalStateException("no signal found")
    }
}