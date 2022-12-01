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

    fun part2(input: List<String>): Int {
        val (topThree, _) = input.fold(
            initial = emptyList<Int>() to 0,
            operation = { (topThree, cnt), s ->
                val calorie = s.toIntOrZero()
                val newCnt = if (s.isEmpty()) 0 else cnt + calorie
                val newTopThree = (topThree + newCnt).sortedDescending().take(3)
                newTopThree to newCnt
            }
        )
        return topThree.sum()
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