import ViewDirection.*

fun main() {
    execute(
        day = "Day08",
        part1 = 21 to ::compute1,
        part1Result = 1736,
        part2 = 8 to ::compute2,
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
            trees.isTreeVisibleFromEdge(rowIdx, colIdx)
        }
    }

    return visibleTrees.count()
}

private fun compute2(input: List<String>): Int {
    val trees: Forrest = input
        .map { row ->
            row.toCharArray()
                .map { it.toString() }
                .map { it.toInt() }
        }

    val viewingScores = trees.flatMapIndexed { rowIdx, treeRow ->
        List(treeRow.size) { colIdx ->
            trees.treeViewingScore(rowIdx, colIdx)
        }
    }

    return viewingScores.max()
}

private fun Forrest.isTreeVisibleFromEdge(rowIdx: Int, colIdx: Int): Boolean {
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

private fun Forrest.treeViewingScore(rowIdx: Int, colIdx: Int): Int {
    val width = this[rowIdx].size
    val tree = this[rowIdx][colIdx]

    val visibleLeft = this[rowIdx].sublistOrEmpty(0, colIdx).visibleTrees(tree, LEFT)
    val visibleRight = this[rowIdx].sublistOrEmpty(colIdx + 1, width).visibleTrees(tree, RIGHT)
    val visibleUp = this
        .filterIndexed { y, _ -> y < rowIdx }
        .map { it[colIdx] }
        .visibleTrees(tree, UP)
    val visibleDown = this
        .filterIndexed { y, _ -> y > rowIdx }
        .map { it[colIdx] }
        .visibleTrees(tree, DOWN)

    val score = visibleLeft.size * visibleRight.size * visibleUp.size * visibleDown.size
    return score
}

private fun List<Int>.visibleTrees(treeHeight: Int, direction: ViewDirection): List<Int> {
    val indices = when (direction) {
        LEFT, UP -> this.indices.reversed()
        RIGHT, DOWN -> this.indices
    }

    val visibleTrees = mutableListOf<Int>()
    for (i in indices) {
        val nextTree = this[i]
        when {
            nextTree >= treeHeight -> {
                visibleTrees.add(nextTree)
                break
            }
            nextTree < treeHeight -> visibleTrees.add(nextTree)
        }
    }
    return visibleTrees
}

private enum class ViewDirection {
    LEFT, UP,
    RIGHT, DOWN
}

typealias Forrest = List<List<Int>>