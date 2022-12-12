import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day12 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(0, compute(testInput, Coordinates(0, 0)))
        assertEquals(0, compute(puzzleInput, Coordinates(20, 0)))
    }

    @Test
    fun part2() {
        assertEquals(0, compute(testInput, Coordinates(0, 0)))
        assertEquals(0, compute(puzzleInput, Coordinates(20, 0)))
    }

    private fun compute(input: List<String>, startPos: Coordinates): Int {
        val relief = input.mapIndexed { rowIdx, line ->
            line.mapIndexed { colIdx, point ->
                val height = point.height()
                val heightL = input.getOrNull(rowIdx)?.getOrNull(colIdx - 1)?.height() ?: Int.MAX_VALUE
                val heightR = input.getOrNull(rowIdx)?.getOrNull(colIdx + 1)?.height() ?: Int.MAX_VALUE
                val heightU = input.getOrNull(rowIdx - 1)?.getOrNull(colIdx)?.height() ?: Int.MAX_VALUE
                val heightD = input.getOrNull(rowIdx + 1)?.getOrNull(colIdx)?.height() ?: Int.MAX_VALUE
                Node(
                    point = point,
                    l = heightL - height,
                    r = heightR - height,
                    u = heightU - height,
                    d = heightD - height,
                )
            }
        }

        return 0
    }

    private fun Char.height() = (if (this == 'S') 'a' else if (this == 'E') 'z' else this).code

    private data class Coordinates(val rowIdx: Int, val colIdx: Int)

    private data class Node(
        val point: Char,
        val l: Int,
        val r: Int,
        val u: Int,
        val d: Int,
    ) {
        val isEnd get() = point == 'E'
    }
}
