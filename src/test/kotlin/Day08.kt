import Day08.Forrest.ViewDirection.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day08 : AbstractDay() {
    @Test
    fun tests() {
        assertEquals(21, compute1(testInput))
        assertEquals(1736, compute1(puzzleInput))

        assertEquals(8, compute2(testInput))
        assertEquals(268800, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val forrest = input.convert()
        return forrest.countTrue { coordinates ->
            forrest.isTreeVisibleFromEdge(coordinates)
        }
    }

    private fun compute2(input: List<String>): Int {
        val forrest = input.convert()
        return forrest.max { coordinates ->
            forrest.treeViewingScore(coordinates)
        }
    }

    private fun List<String>.convert(): Forrest {
        val trees = this.map { row ->
            row.map { Tree(it.digitToInt()) }
        }
        return Forrest(trees)
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
            fun List<Tree>.filterVisible(tree: Tree): List<Tree> {
                val visibleTrees = mutableListOf<Tree>()
                for (nextTree in this) {
                    visibleTrees.add(nextTree)
                    if (nextTree >= tree) {
                        break
                    }
                }
                return visibleTrees
            }

            val tree = treeAt(coordinates)
            val visibleLeft = treesFrom(coordinates, LEFT).filterVisible(tree)
            val visibleRight = treesFrom(coordinates, RIGHT).filterVisible(tree)
            val visibleUp = treesFrom(coordinates, UP).filterVisible(tree)
            val visibleDown = treesFrom(coordinates, DOWN).filterVisible(tree)

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

        private enum class ViewDirection {
            LEFT, UP,
            RIGHT, DOWN
        }
    }

    @JvmInline
    value class Tree(private val height: Int) {
        operator fun compareTo(it: Tree): Int = height.compareTo(it.height)
    }
}
