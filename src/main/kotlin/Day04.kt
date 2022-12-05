fun main() {
    execute(
        day = "Day04",
        part1 = 2 to ::compute1,
        part2 = 4 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int {
    return input
        .map { it.split(",") }
        .count { (a, b) ->
            val (a1, a2) = a.split("-").map { it.toInt() }
            val (b1, b2) = b.split("-").map { it.toInt() }
            (a1 >= b1 && a2 <= b2) || (b1 >= a1 && b2 <= a2)
        }
}

private fun compute2(input: List<String>): Int {
    return input
        .map { it.split(",") }
        .count { (a, b) ->
            val (a1, a2) = a.split("-").map { it.toInt() }
            val (b1, b2) = b.split("-").map { it.toInt() }
            a1 in b1..b2 || a2 in b1..b2 || b1 in a1..a2 || b2 in a1..a2
        }
}
