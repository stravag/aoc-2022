fun main() {
    execute(
        day = "Day08",
        part1 = 0 to ::compute1,
        part2 = 0 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int {
    return input.size
}

private fun compute2(input: List<String>): Int {
    return input.size
}
