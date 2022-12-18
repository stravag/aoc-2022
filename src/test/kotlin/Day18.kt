import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

object Day18 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(64, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(4310, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(58, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        var surfaceArea = 0
        val blocks = mutableSetOf<Block>()
        input
            .map { Block.of(it) }
            .forEach { block ->
                val connectedBlocks = block.adjacentBlocks()
                    .count { blocks.contains(it) }

                surfaceArea -= connectedBlocks
                surfaceArea += 6 - connectedBlocks
                blocks.add(block)
            }

        return surfaceArea
    }

    private fun compute2(input: List<String>): Int {
        var surfaceArea = 0
        val blocks = mutableSetOf<Block>()
        input
            .map { Block.of(it) }
            .forEach { block ->
                val connectedBlocks = block.adjacentBlocks()
                    .count { blocks.contains(it) }

                val airBubbles = block.adjacentBlocks().count { isTrapped(it, blocks + block) }
                surfaceArea -= airBubbles * 6
                surfaceArea -= connectedBlocks
                surfaceArea += 6 - connectedBlocks
                blocks.add(block)
            }

        return surfaceArea
    }

    private fun isTrapped(block: Block, blocks: Set<Block>): Boolean {
        return !blocks.contains(block) && block.adjacentBlocks().all { blocks.contains(it) }
    }

    data class Block(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {
        fun adjacentBlocks(): List<Block> {
            return listOf(
                copy(x = x + 1),
                copy(x = x - 1),
                copy(y = y + 1),
                copy(y = y - 1),
                copy(z = z + 1),
                copy(z = z - 1),
            )
        }

        companion object {
            fun of(s: String): Block {
                return s.split(",").let { (x, y, z) ->
                    Block(x.toInt(), y.toInt(), z.toInt())
                }
            }
        }
    }
}
