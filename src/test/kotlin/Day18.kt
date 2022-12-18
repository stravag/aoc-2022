import org.junit.jupiter.api.Test
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
        assertEquals(58, compute2Attempt1(testInput))
        assertEquals(58, compute2Attempt2(testInput))
    }

    @Test
    fun part2PuzzleAttemp1() {
        assertEquals(1, compute2Attempt1(puzzleInput))
    }

    @Test
    fun part2PuzzleAttemp2() {
        assertEquals(1, compute2Attempt2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        var surfaceArea = 0
        val blocks = mutableSetOf<Block>()
        input
            .map { Block.of(it) }
            .forEach { block ->
                val connectedBlocks = block.adjacentBlocks
                    .count { blocks.contains(it) }

                surfaceArea -= connectedBlocks
                surfaceArea += 6 - connectedBlocks
                blocks.add(block)
            }

        return surfaceArea
    }

    private fun compute2Attempt1(input: List<String>): Int {
        var surfaceArea = 0
        val blocks = mutableSetOf<Block>()
        input
            .map { Block.of(it) }
            .forEach { block ->
                val connectedBlocks = block.adjacentBlocks
                    .count { blocks.contains(it) }

                val airBubbles = block.adjacentBlocks.count { isTrapped(it, blocks + block) }
                surfaceArea -= airBubbles * 6
                surfaceArea -= connectedBlocks
                surfaceArea += 6 - connectedBlocks
                blocks.add(block)
            }

        return surfaceArea
    }

    private fun compute2Attempt2(input: List<String>): Int {
        val surfaceArea = compute1(input)
        val blocks = input
            .map { Block.of(it) }
            .toSet()

        val airBubbles = findAirBubbles(blocks)

        return surfaceArea - airBubbles * 6
    }

    private fun findAirBubbles(blocks: Set<Block>): Int {
        val foundAirBubbles = mutableSetOf<Block>()
        val queue = mutableListOf<Block>()
        val explored = mutableSetOf<Block>()
        queue.add(blocks.first())
        while (queue.isNotEmpty()) {
            val currentBlock = queue.removeFirst()
            val airBubbles = findAirBubblesNextTo(currentBlock, blocks)
            foundAirBubbles.addAll(airBubbles)
            if (explored.size == blocks.size) {
                return foundAirBubbles.size
            }
            currentBlock
                .adjacentBlocks
                .filter { blocks.contains(it) }
                .forEach { adjacentBlock ->
                    if (!explored.contains(adjacentBlock)) {
                        explored.add(adjacentBlock)
                        queue.add(adjacentBlock)
                    }
                }
        }
        return foundAirBubbles.size
    }

    private fun findAirBubblesNextTo(block: Block, blocks: Set<Block>): Set<Block> {
        return block.adjacentBlocks
            .filter { isTrapped(it, blocks) }
            .toSet()
    }

    private fun isTrapped(block: Block, blocks: Set<Block>): Boolean {
        return !blocks.contains(block) && block.adjacentBlocks.all { blocks.contains(it) }
    }

    data class Block(
        val x: Int,
        val y: Int,
        val z: Int,
    ) {
        val adjacentBlocks: List<Block>
            get() {
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
