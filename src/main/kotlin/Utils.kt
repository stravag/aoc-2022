import java.io.File

fun <R1, R2> execute(
    day: String,
    part1: Pair<R1, (List<String>) -> R1>, part1Test: List<String> = readInput("${day}_test.txt"),
    part2: Pair<R2, (List<String>) -> R2>, part2Test: List<String> = readInput("${day}_test.txt"),
) {
    val (expected1, method1) = part1
    val answer1 = method1(part1Test)
    check(expected1 == answer1) { "expected $expected1 but got $answer1" }
    println("part1: " + method1(readInput("${day}.txt")))

    val (expected2, method2) = part2
    val answer2 = method2(part2Test)
    check(expected2 == answer2) { "expected $expected2 but got $answer2" }
    println("part2: " + method2(readInput("${day}.txt")))
}

private fun readInput(file: String) = File("src/main/resources/$file").readLines()

fun <T> List<T>.sublistOrNull(fromIndex: Int, toIndex: Int) = try {
    this.subList(fromIndex, toIndex)
} catch (e: Exception) {
    null
}
