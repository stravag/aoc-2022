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
    return forrest.countTrue { coordinates ->
        forrest.isTreeVisibleFromEdge(coordinates)
    }
}

private fun compute2(input: List<String>): Int {
    val trees: Forrest = input.convert()
    return trees.max { coordinates ->
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

private fun List<Tree>.visibleTrees(tree: Tree): List<Tree> {
    val visibleTrees = mutableListOf<Tree>()
    for (nextTree in this) {
        visibleTrees.add(nextTree)
        if (nextTree >= tree) {
            break
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

    fun countTrue(transform: (Coordinates) -> Boolean): Int {
        return map(transform).count { it }
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
        val visibleLeft = treesFrom(coordinates, LEFT).visibleTrees(tree)
        val visibleRight = treesFrom(coordinates, RIGHT).visibleTrees(tree)
        val visibleUp = treesFrom(coordinates, UP).visibleTrees(tree)
        val visibleDown = treesFrom(coordinates, DOWN).visibleTrees(tree)

        return visibleLeft.size * visibleRight.size * visibleUp.size * visibleDown.size
    }

    private fun treeAt(coordinates: Coordinates) = trees[coordinates.rowIdx][coordinates.colIdx]

    private fun treesFrom(coordinates: Coordinates, direction: ViewDirection): List<Tree> {
        val (rowIdx, colIdx) = coordinates
        val width = trees[rowIdx].size
        val height = trees.size
        return when (direction) {
            LEFT -> trees[rowIdx].sublistOrEmpty(0, colIdx).reversed()
            UP -> trees.sublistOrEmpty(0, rowIdx).map { it[colIdx] }.reversed()
            RIGHT -> trees[rowIdx].sublistOrEmpty(colIdx + 1, width)
            DOWN -> trees.sublistOrEmpty(rowIdx + 1, height).map { it[colIdx] }
        }
    }
}

@JvmInline
value class Tree(private val height: Int) {
    operator fun compareTo(it: Tree): Int = height.compareTo(it.height)
}
