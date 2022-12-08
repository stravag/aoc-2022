import java.io.File

fun <R1, R2> execute(
    day: String,
    part1: Pair<R1, (List<String>) -> R1>,
    part1Test: List<String> = readInput("${day}_test.txt"),
    part1Result: R1? = null,
    part2: Pair<R2, (List<String>) -> R2>,
    part2Test: List<String> = readInput("${day}_test.txt"),
    part2Result: R2? = null,
) {
    val (expected1, method1) = part1
    val testAnswer1 = method1(part1Test)
    val answer1 = method1(readInput("${day}.txt"))
    check(expected1 == testAnswer1) { "expected $expected1 but got $testAnswer1" }
    part1Result?.let { expectedResult1 ->
        check(expectedResult1 == answer1) { "expected $expectedResult1 but got $answer1" }
    }
    println("part1: $answer1")

    val (expected2, method2) = part2
    val testAnswer2 = method2(part2Test)
    val answer2 = method2(readInput("${day}.txt"))
    check(expected2 == testAnswer2) { "expected $expected2 but got $testAnswer2" }
    part2Result?.let { expectedResult2 ->
        check(expectedResult2 == answer2) { "expected $expectedResult2 but got $answer2" }
    }
    println("part2: $answer2")
}

private fun readInput(file: String) = File("src/main/resources/$file").readLines()

fun <T> List<T>.sublistOrNull(fromIndex: Int, toIndex: Int) = try {
    this.subList(fromIndex, toIndex)
} catch (e: Exception) {
    null
}

fun <T> List<T>.sublistOrEmpty(fromIndex: Int, toIndex: Int) = this.sublistOrNull(fromIndex, toIndex) ?: emptyList()
