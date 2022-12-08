fun main() {
    execute(
        day = "Day04",
        part1 = 2 to ::compute1,
        part2 = 4 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int = input.parse()
    .count { (a, b) -> a.contains(b) || b.contains(a) }

private fun compute2(input: List<String>): Int = input.parse()
    .count { (a, b) -> a.overlaps(b) || b.overlaps(a) }

private fun List<String>.parse(): List<Pair<IntRange, IntRange>> = this
    .map { it.split(",") }
    .map { (a, b) -> Pair(a.toIntRange(), b.toIntRange()) }

private fun String.toIntRange(): IntRange = this
    .split("-")
    .map { it.toInt() }
    .let { (a, b) -> a .. b }

private fun IntRange.contains(other: IntRange): Boolean {
    return contains(other.first) && contains(other.last)
}

private fun IntRange.overlaps(other: IntRange): Boolean {
    return contains(other.first) || contains(other.last)
}
