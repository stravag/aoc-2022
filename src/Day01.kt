import java.lang.Exception
import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
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

    fun elfCalories(offset: Int = 0, input: List<String>): List<Int> {
        val remainingElfCalories = input.safeSubList(offset, input.size)
            ?: return emptyList()

        val elfCalories = remainingElfCalories
            .takeWhile { it.isNotEmpty() }
            .map { it.toInt() }

        val newOffset = offset + elfCalories.size + 1

        return elfCalories(newOffset, input) + elfCalories.sum()
    }

    fun part2(input: List<String>): Int {
        val elfCalories = elfCalories(input = input)
        return elfCalories
            .sortedDescending()
            .take(3)
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

fun String.toIntOrZero() = this.ifEmpty { "0" }.toInt()

 fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int) = try {
     this.subList(fromIndex, toIndex)
 } catch (e: Exception) {
     null
 }