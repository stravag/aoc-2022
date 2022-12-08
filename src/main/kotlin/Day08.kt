fun main() {
    execute(
        day = "Day08",
        part1 = 21 to ::compute1,
        part2 = 0 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int {
    val trees: Forrest = input
        .map { row -> row.toCharArray().map { it.code } }

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
    val tree = this[rowIdx][colIdx]
    val neighbors = getNeighborHeights(rowIdx, colIdx)
    return neighbors.any { it < tree }
}

private fun Forrest.getNeighborHeights(rowIdx: Int, colIdx: Int): List<Int> {
    val row = this[rowIdx]
    val topRow = getOrNull(rowIdx - 1).orEmpty()
    val bottomRow = getOrNull(rowIdx - 1).orEmpty()

    return listOf(
        row.getOrNull(colIdx - 1) ?: 0, // left
        topRow.getOrNull(colIdx) ?: 0, // top
        row.getOrNull(colIdx + 1) ?: 0, // right
        bottomRow.getOrNull(colIdx) ?: 0, // bottom
    )
}

typealias Forrest = List<List<Int>>