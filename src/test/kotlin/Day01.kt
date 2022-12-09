import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

object Day01 : AbstractDay() {
    @Test
    fun tests() {
        assertEquals(24000, part1(testInput))
        assertEquals(70374, part1(puzzleInput))

        assertEquals(45000, part2(testInput))
        assertEquals(204610, part2(puzzleInput))
    }

    private fun part1(input: List<String>): Int {
        val (max, _) = input.fold(
            initial = 0 to 0,
            operation = { (max, cnt), s ->
                val calorie = s.toIntOrZero()
                val newCnt = if (s.isEmpty()) 0 else cnt + calorie
                val newMax = max(max, newCnt)
                newMax to newCnt
            }
        )
        return max
    }

    private fun part2(input: List<String>): Int {
        val elfCalories = elfCalories(input = input)
        return elfCalories
            .sortedDescending()
            .take(3)
            .sum()
    }

    private fun elfCalories(offset: Int = 0, input: List<String>): List<Int> {
        val remainingElfCalories = input.sublistOrNull(offset, input.size)
            ?: return emptyList()

        val elfCalories = remainingElfCalories
            .takeWhile { it.isNotEmpty() }
            .map { it.toInt() }

        val newOffset = offset + elfCalories.size + 1

        return elfCalories(newOffset, input) + elfCalories.sum()
    }

    private fun String.toIntOrZero() = this.ifEmpty { "0" }.toInt()
}
