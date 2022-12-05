object Day05 {
    fun compute1(input: List<String>): String {
        val (stacks, moves) = input.parse()

        moves.forEach {
            applyMove(it, stacks)
        }

        return stacks.readAnswer()
    }

    fun compute2(input: List<String>): String {
        val (stacks, moves) = input.parse()

        moves.forEach {
            applyMove2(it, stacks)
        }

        return stacks.readAnswer()
    }

    private fun applyMove(move: String, stacks: Stacks) {
        val (count, from, to) = move.parse()
        repeat(count) {
            val toMove = stacks[from - 1].removeLast()
            stacks[to - 1].addLast(toMove)
        }
    }

    private fun applyMove2(move: String, stacks: Stacks) {
        val (count, from, to) = move.parse()
        stacks[from - 1]
            .takeLast(count)
            .forEach {
                stacks[from - 1].removeLast()
                stacks[to - 1].addLast(it)
            }
    }

    private fun String.parse(): Move {
        val parts = split(" ")
        return Move(
            count = parts[1].toInt(),
            from = parts[3].toInt(),
            to = parts[5].toInt(),
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
            val idx = i + 3 * i + 1
            val char = charArray.elementAtOrNull(idx) ?: ' '
            if (char != ' ') stacks[i].add(char)
        }
    }

    private fun Stacks.readAnswer(): String {
        return map { it.last() }
            .joinToString("")
    }

    data class Move(val count: Int, val from: Int, val to: Int)
}

typealias Stacks = List<ArrayDeque<Char>>