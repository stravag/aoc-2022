import kotlin.math.max

fun main() {
    execute(
        day = "Day01",
        part1 = Part(
            expectedTestResult = 24000,
            expectedResult = 70374,
            compute = ::part1
        ),
        part2 = Part(
            expectedTestResult = 45000,
            expectedResult = 204610,
            compute = ::part2
        ),
    )
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
