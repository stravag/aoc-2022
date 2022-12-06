fun main() {
    execute(
        day = "Day06",
        part1 = listOf(5, 6, 10, 11) to ::compute1,
        part1Test = listOf(
            "bvwbjplbgvbhsrlpgdmjqwftvncz",
            "nppdvjthqldpwncqszvftbrmjlhg",
            "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg",
            "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw",
        ),
        part2 = listOf(19, 23, 23, 29, 26) to ::compute2,
        part2Test = listOf(
            "mjqjpqmgbljsphdztnvjfqwrcgsmlb",
            "bvwbjplbgvbhsrlpgdmjqwftvncz",
            "nppdvjthqldpwncqszvftbrmjlhg",
            "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg",
            "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw",
        ),
    )
}

private fun compute1(input: List<String>): List<Int> {
    return input.map { it.findSignalLoc(4) }
}

private fun compute2(input: List<String>): List<Int> {
    return input.map { it.findSignalLoc(14) }
}

private fun String.findSignalLoc(signalSize: Int): Int {
    for (i in signalSize .. length) {
        val chunk = subSequence(i - signalSize, i)
        if (chunk.toSet().size == signalSize) return i
    }
    throw IllegalStateException("no signal found")
}
