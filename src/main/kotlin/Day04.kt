object Day04 {
    fun compute1(input: List<String>): Int {
        return input
            .map { it.split(",") }
            .count { (a, b) ->
                val (a1, a2) = a.split("-").map { it.toInt() }
                val (b1, b2) = b.split("-").map { it.toInt() }
                (a1 >= b1 && a2 <= b2) || (b1 >= a1 && b2 <= a2)
            }
    }

    fun compute2(input: List<String>): Int {
        return input
            .map { it.split(",") }
            .count { (a, b) ->
                val (a1, a2) = a.split("-").map { it.toInt() }
                val (b1, b2) = b.split("-").map { it.toInt() }
                a1 in b1..b2 || a2 in b1..b2 || b1 in a1..a2 || b2 in a1..a2
            }
    }
}
