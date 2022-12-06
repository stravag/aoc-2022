fun main() {
    execute(
        day = "Day05",
        part1 = "CMZ" to ::compute1,
        part2 = "MCD" to ::compute2,
    )
}

private fun compute1(input: List<String>): String {
    val (stacks, moves) = input.parse()

    moves.forEach {
        applyMove(it, stacks)
    }

    return stacks.readAnswer()
}

private fun compute2(input: List<String>): String {
    val (stacks, moves) = input.parse()

    moves.forEach {
        applyMove2(it, stacks)
    }

    return stacks.readAnswer()
}

private fun applyMove(move: String, stacks: Stacks) {
    val (count, from, to) = move.parse()
    repeat(count) {
        val toMove = stacks[from].removeLast()
        stacks[to].addLast(toMove)
    }
}

private fun applyMove2(move: String, stacks: Stacks) {
    val (count, from, to) = move.parse()
    stacks[from]
        .takeLast(count)
        .forEach {
            stacks[from].removeLast()
            stacks[to].addLast(it)
        }
}

private fun String.parse(): Move {
    val parts = split(" ")
    return Move(
        count = parts[1].toInt(),
        from = parts[3].toInt() - 1,
        to = parts[5].toInt() - 1,
    )
}

private fun List<String>.parse(): Pair<Stacks, List<String>> {
    val stackData = this.takeWhile { it.isNotBlank() }

    val numberOfStacks = stackData
        .last()
        .split(" ")
        .last { it.isNotBlank() }
        .toInt()

    val stacks = List(numberOfStacks) { ArrayDeque<Char>() }
    stackData
        .dropLast(1)
        .reversed()
        .forEach {
            it.putOnStacks(stacks)
        }

    val moves = this.drop(stackData.size + 1)

    return stacks to moves
}

private fun String.putOnStacks(stacks: Stacks) {
    val charArray = this.toCharArray()
    for (i in stacks.indices) {
        val idx = 4 * i + 1
        val char = charArray.elementAtOrNull(idx) ?: ' '
        if (char != ' ') stacks[i].add(char)
    }
}

private fun Stacks.readAnswer(): String {
    return map { it.last() }
        .joinToString("")
}

private data class Move(val count: Int, val from: Int, val to: Int)

private typealias Stacks = List<ArrayDeque<Char>>