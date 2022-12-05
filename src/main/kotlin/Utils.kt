import java.io.File

fun <R1, R2> execute(
    day: String,
    part1: Pair<R1, (List<String>) -> R1>,
    part2: Pair<R2, (List<String>) -> R2>,
) {
    check(part1.first == part1.second(readInput("${day}_test.txt")))
    println("part1: " + part1.second(readInput("${day}.txt")))

    check(part2.first == part2.second(readInput("${day}_test.txt")))
    println("part2: " + part2.second(readInput("${day}.txt")))
}

private fun readInput(file: String) = File("src/main/resources/$file").readLines()
