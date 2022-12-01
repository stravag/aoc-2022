import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
        var maxCalories = 0
        var caloriesCount = 0
        input.forEach {
            if (it.isBlank()) {
                maxCalories = max(caloriesCount, maxCalories)
                caloriesCount = 0
            } else {
                caloriesCount += it.toInt()
            }
        }
        return maxCalories
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
