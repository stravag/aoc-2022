import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day12 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(31, compute1(testInput))
        assertEquals(437, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(29, compute2(testInput))
        assertEquals(430, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val relief = buildRelief(input)
        val startNode = relief.flatten().single { it.isStart }

        return findShortestPaths(relief, startNode)
            .single { it.isEnd }.distance
    }

    private fun compute2(input: List<String>): Int {
        val relief = buildRelief(input)
        val lowNodes = relief.flatten().filter { it.isLow }
        return lowNodes
            .flatMap { findShortestPaths(relief, it) }
            .filter { it.isEnd }
            .minBy { it.distance }
            .distance
    }

    private fun buildRelief(input: List<String>): List<List<Node>> {
        return input.mapIndexed { rowIdx, line ->
            line.mapIndexed { colIdx, point ->
                val height = point.height()
                val coordinates = Coordinates(rowIdx, colIdx)
                val adjacentCoordinates = coordinates.getAdjacentCoordinates()
                val accessibleNeighbors = adjacentCoordinates
                    .mapNotNull { c -> input.get(c)?.let { c to it.height() } }
                    .map { (c, adjacentHeight) -> c to (adjacentHeight - height) }
                    .filter { (_, adjacentHeightDiff) -> adjacentHeightDiff <= 1 }
                    .map { it.first }

                Node(point, coordinates, accessibleNeighbors)
            }
        }
    }

    private fun List<String>.get(coordinates: Coordinates): Char? {
        return this.getOrNull(coordinates.rowIdx)?.getOrNull(coordinates.colIdx)
    }

    private fun List<List<Node>>.get(coordinates: Coordinates): Node {
        return this[coordinates.rowIdx][coordinates.colIdx]
    }

    private fun findShortestPaths(relief: List<List<Node>>, startNode: Node): Set<Node> {
        startNode.distance = 0
        val unsettled = mutableSetOf(startNode)
        val settled = mutableSetOf<Node>()
        while (unsettled.isNotEmpty()) {
            val currentNode = unsettled.minBy { it.distance }
            unsettled.remove(currentNode)
            currentNode.accessibleNeighbors.forEach {
                val neighborNode = relief.get(it)
                if (!settled.contains(neighborNode)) {
                    val sourceDistance = currentNode.distance
                    if (sourceDistance + 1 < neighborNode.distance) {
                        neighborNode.distance = sourceDistance + 1
                    }
                    unsettled.add(neighborNode)
                }
            }
            settled.add(currentNode)
        }
        return settled
    }

    private fun Char.height() = (if (this == 'S') 'a' else if (this == 'E') 'z' else this).code

    private data class Coordinates(val rowIdx: Int, val colIdx: Int) {
        fun getAdjacentCoordinates(): List<Coordinates> {
            return listOf(
                Coordinates(rowIdx, colIdx - 1), // left
                Coordinates(rowIdx, colIdx + 1), // right
                Coordinates(rowIdx + 1, colIdx), // up
                Coordinates(rowIdx - 1, colIdx), // down
            )
        }
    }

    private data class Node(
        val point: Char,
        val coordinates: Coordinates,
        val accessibleNeighbors: List<Coordinates>,
        var distance: Int = Int.MAX_VALUE,
    ) {
        val isStart get() = point == 'S'
        val isEnd get() = point == 'E'
        val isLow get() = point.height() == 'a'.height()
    }
}
