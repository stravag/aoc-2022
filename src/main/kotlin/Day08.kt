import ViewDirection.*

fun main() {
    execute(
        day = "Day08",
        part1 = 21 to ::compute1,
        part1Result = 1736,
        part2 = 8 to ::compute2,
        part2Result = 268800
    )
}

private fun compute1(input: List<String>): Int {
    val forrest = input.convert()
    return forrest.count { coordinates ->
        forrest.isTreeVisibleFromEdge(coordinates)
    }
}

private fun compute2(input: List<String>): Int {
    val trees: Forrest = input.convert()
    return  trees.max { coordinates ->
        trees.treeViewingScore(coordinates)
    }
}

private fun List<String>.convert(): Forrest {
    val trees = this.map { row ->
        row.toCharArray()
            .map { it.toString() }
            .map { Tree(it.toInt()) }
    }
    return Forrest(trees)
}

private fun List<Tree>.visibleTrees(tree: Tree, direction: ViewDirection): List<Tree> {
    val indices = when (direction) {
        LEFT, UP -> this.indices.reversed()
        RIGHT, DOWN -> this.indices
    }

    val visibleTrees = mutableListOf<Tree>()
    for (i in indices) {
        val nextTree = this[i]
        when {
            nextTree >= tree -> {
                visibleTrees.add(nextTree)
                break
            }

            nextTree < tree -> visibleTrees.add(nextTree)
        }
    }
    return visibleTrees
}

private enum class ViewDirection {
    LEFT, UP,
    RIGHT, DOWN
}

private data class Coordinates(val rowIdx: Int, val colIdx: Int)

private data class Forrest(
    val trees: List<List<Tree>>
) {
    private fun <R> map(transform: (Coordinates) -> R): List<R> {
        return trees.flatMapIndexed { rowIdx, treeRow ->
            List(treeRow.size) { colIdx ->
                transform(Coordinates(rowIdx, colIdx))
            }
        }
    }

    fun <R> count(transform: (Coordinates) -> R): Int {
        return map(transform).count()
    }

    fun max(transform: (Coordinates) -> Int): Int {
        return map(transform).max()
    }

    fun isTreeVisibleFromEdge(coordinates: Coordinates): Boolean {
        val tree = treeAt(coordinates)
        val leftTrees = treesFrom(coordinates, LEFT)
        val rightTrees = treesFrom(coordinates, RIGHT)
        val topTrees = treesFrom(coordinates, UP)
        val bottomTrees = treesFrom(coordinates, DOWN)

        return leftTrees.all { tree > it } ||
                rightTrees.all { tree > it } ||
                topTrees.all { tree > it } ||
                bottomTrees.all { tree > it }
    }

    fun treeViewingScore(coordinates: Coordinates): Int {
        val tree = treeAt(coordinates)
        val visibleLeft = treesFrom(coordinates, LEFT).visibleTrees(tree, LEFT)
        val visibleRight = treesFrom(coordinates, RIGHT).visibleTrees(tree, RIGHT)
        val visibleUp = treesFrom(coordinates, UP).visibleTrees(tree, UP)
        val visibleDown = treesFrom(coordinates, DOWN).visibleTrees(tree, DOWN)

        return visibleLeft.size * visibleRight.size * visibleUp.size * visibleDown.size
    }

    private fun treeAt(coordinates: Coordinates) = trees[coordinates.rowIdx][coordinates.colIdx]

    private fun treesFrom(coordinates: Coordinates, direction: ViewDirection): List<Tree> {
        val (rowIdx, colIdx) = coordinates
        val width = trees[rowIdx].size
        return when (direction) {
            LEFT -> trees[rowIdx].sublistOrEmpty(0, colIdx)
            UP -> trees.filterIndexed { y, _ -> y < rowIdx }.map { it[colIdx] }
            RIGHT -> trees[rowIdx].sublistOrEmpty(colIdx + 1, width)
            DOWN -> trees.filterIndexed { y, _ -> y > rowIdx }.map { it[colIdx] }
        }
    }
}

@JvmInline
value class Tree(private val height: Int) {
    operator fun compareTo(it: Tree): Int = height.compareTo(it.height)
}
