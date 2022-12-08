fun main() {
    execute(
        day = "Day08",
        part1 = 21 to ::compute1,
        part2 = 0 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int {
    val trees: Forrest = input
        .map { row ->
            row.toCharArray()
                .map { it.toString() }
                .map { it.toInt() }
        }

    val visibleTrees = trees.flatMapIndexed { rowIdx, treeRow ->
        treeRow.filterIndexed { colIdx, _ ->
            trees.isVisible(rowIdx, colIdx)
        }
    }

    return visibleTrees.count()
}

private fun compute2(input: List<String>): Int {
    return input.size
}

private fun Forrest.isVisible(rowIdx: Int, colIdx: Int): Boolean {
    val width = this[rowIdx].size
    val tree = this[rowIdx][colIdx]

    val leftTrees = this[rowIdx].sublistOrNull(0, colIdx) ?: listOf(-1)
    val rightTrees = this[rowIdx].sublistOrNull(colIdx + 1, width) ?: listOf(-1)
    val topTrees = this
        .filterIndexed { y, _ -> y < rowIdx }
        .map { it[colIdx] }
        .ifEmpty { listOf(-1) }
    val bottomTrees = this
        .filterIndexed { y, _ -> y > rowIdx }
        .map { it[colIdx] }
        .ifEmpty { listOf(-1) }

    return leftTrees.all { tree > it } || rightTrees.all { tree > it } || topTrees.all { tree > it } || bottomTrees.all { tree > it }

}

typealias Forrest = List<List<Int>>