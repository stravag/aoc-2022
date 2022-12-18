import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
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
        assertEquals(58, compute2Attempt3(testInput))
    }

    @Test
    fun part2PuzzleAttempt1() {
        assertEquals(1, compute2Attempt1(puzzleInput))
    }

    @Test
    fun part2PuzzleAttempt2() {
        assertEquals(1, compute2Attempt2(puzzleInput))
    }

    @Test
    fun part2PuzzleAttempt3() {
        assertEquals(1, compute2Attempt3(puzzleInput))
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

    private fun compute2Attempt3(input: List<String>): Int {
        val container = Container()
        val rocks = input
            .map { Block.of(it) }
            .onEach { container.adjustTo(it) }
            .toSet()

        container.rocks = rocks
        container.expandWater()

        return container.surfaceCount
    }

    private class Container {
        var surfaceCount = 0
        var rocks: Set<Block> = emptySet()

        private var minPos = Block(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE)
        private var maxPos = Block(Int.MIN_VALUE, Int.MIN_VALUE, Int.MIN_VALUE)

        fun adjustTo(block: Block) {
            // container should include a padding of 1 hence the 1 correction
            minPos = Block(
                x = min(minPos.x, block.x - 1),
                y = min(minPos.y, block.y - 1),
                z = min(minPos.z, block.z - 1),
            )
            maxPos = Block(
                x = max(maxPos.x, block.x + 1),
                y = max(maxPos.y, block.y + 1),
                z = max(maxPos.z, block.z + 1),
            )
        }

        fun expandWater() {
            val queue = mutableListOf<Block>()
            val water = mutableSetOf<Block>()
            queue.add(minPos)
            while (queue.isNotEmpty()) {
                val currentWater = queue.removeFirst()
                val blocksNextToWater = currentWater.adjacentBlocks
                    .filter { isInBounds(it) }
                    .count { rocks.contains(it) }
                surfaceCount += blocksNextToWater

                currentWater
                    .adjacentBlocks
                    .filter { isInBounds(it) }
                    .filterNot { rocks.contains(it) }
                    .forEach { adjacentWater ->
                        if (!water.contains(adjacentWater)) {
                            water.add(adjacentWater)
                            queue.add(adjacentWater)
                        }
                    }
            }
        }

        private fun isInBounds(block: Block): Boolean {
            val xInbound = minPos.x <= block.x && block.x <= maxPos.x
            val yInbound = minPos.y <= block.y && block.y <= maxPos.y
            val zInbound = minPos.z <= block.z && block.z <= maxPos.z
            return xInbound && yInbound && zInbound
        }
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
