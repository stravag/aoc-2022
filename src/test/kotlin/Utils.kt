import java.io.File


fun <R1, R2> execute(
    day: String,
    part1: Part<R1>,
    part1TestData: List<String> = readInput("${day}_test.txt"),
    part2: Part<R2>,
    part2TestData: List<String> = readInput("${day}_test.txt"),
) {

    val puzzleData = readInput("${day}.txt")
    val result1 = runPart(
        part1TestData,
        puzzleData,
        part1.compute,
        part1.expectedTestResult,
        part1.expectedResult
    )
    println("part1: $result1")

    val result2 = runPart(
        part2TestData,
        puzzleData,
        part2.compute,
        part2.expectedTestResult,
        part2.expectedResult
    )
    println("part2: $result2")
}

private fun <R> runPart(
    testData: List<String>,
    puzzleData: List<String>,
    compute: (List<String>) -> R,
    expectedTestResult: R,
    expectedResult: R
): R {
    compute(testData).also { check(it == expectedTestResult) { "expected $expectedTestResult but got $it" } }
    return compute(puzzleData).also { check(it == expectedResult) { "expected $expectedResult but got $it" } }
}


data class Part<R>(
    val expectedTestResult: R,
    val expectedResult: R,
    val compute: (List<String>) -> R,
)

private fun readInput(file: String) = File("src/main/resources/$file").readLines()

fun <T> List<T>.sublistOrNull(fromIndex: Int, toIndex: Int) = try {
    this.subList(fromIndex, toIndex)
} catch (e: Exception) {
    null
}

fun <T> List<T>.sublistOrEmpty(fromIndex: Int, toIndex: Int) = this.sublistOrNull(fromIndex, toIndex) ?: emptyList()
