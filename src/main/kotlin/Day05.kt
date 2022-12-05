object Day05 {
    fun compute1(input: List<String>): String {
        val stackData = input
            .takeWhile { it.isNotBlank() }

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

        input
            .drop(stackData.size + 1)
            .forEach {
                applyMove(it, stacks)
            }

        return stacks
            .map { it.last() }
            .joinToString("")
    }

    private fun applyMove(move: String, stacks: List<ArrayDeque<Char>>) {
        val (count, from, to) = move.split(" ").filter { it.isNumber() }.map { it.toInt() }
        repeat(count) {
            val toMove = stacks[from - 1].removeLast()
            stacks[to - 1].addLast(toMove)
        }
    }

    private fun String.putOnStacks(stacks: List<MutableList<Char>>) {
        val charArray = this.toCharArray()
        for (i in stacks.indices) {
            val idx = i + 3 * i + 1
            val char = charArray[idx]
            if (char != ' ') stacks[i].add(char)
        }
    }

    private fun String.isNumber() = try {
        this.toInt()
        true
    } catch (e: Exception) {
        false
    }
}
