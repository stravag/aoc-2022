import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.*
import kotlin.collections.HashSet
import kotlin.test.assertEquals

class Day24 : AbstractDay() {

    @Test
    fun testNoInfiniteWait() {
        val input = """
            #.###
            #>>>#
            ###.#
        """.trimIndent().lines()

        val (valley, blizzardCache) = parse(input)

        print(valley, blizzardCache, ValleyState(valley.start, 0))

        val quickestPath = findQuickestPath(valley, blizzardCache, 0, valley.start, valley.end)

        assertEquals(Int.MAX_VALUE, quickestPath)
    }

    @Test
    fun loopTest() {
        val input = """
            #.###
            #...#
            #>>>#
            ###.#
        """.trimIndent().lines()

        val (valley, blizzardCache) = parse(input)

        print(valley, blizzardCache, ValleyState(valley.start, 0))

        val quickestPath = findQuickestPath(valley, blizzardCache, 0, valley.start, valley.end)

        assertEquals(Int.MAX_VALUE, quickestPath)
    }

    @Test
    fun simpleTest() {
        val input = """
            #.##
            #..#
            ##.#
        """.trimIndent().lines()

        val (valley, blizzardCache) = parse(input)

        val quickestPath = findQuickestPath(valley, blizzardCache, 0, valley.start, valley.end)

        assertEquals(3, quickestPath)
    }

    @Test
    fun part1Test() {
        assertEquals(18, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(228, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(54, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(723, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val (valley, blizzardCache) = parse(input)
        return findQuickestPath(
            valley = valley,
            blizzardCache = blizzardCache,
            minutesPassed = 0,
            start = valley.start,
            end = valley.end,
        )
    }

    private fun compute2(input: List<String>): Int {
        val (valley, blizzardCache) = parse(input)

        val run1 = findQuickestPath(
            valley = valley,
            blizzardCache = blizzardCache,
            minutesPassed = 0,
            start = valley.start,
            end = valley.end,
        )

        val run2 = findQuickestPath(
            valley = valley,
            blizzardCache = blizzardCache,
            minutesPassed = run1,
            start = valley.end,
            end = valley.start,
        )

        return findQuickestPath(
            valley = valley,
            blizzardCache = blizzardCache,
            minutesPassed = run2,
            start = valley.start,
            end = valley.end,
        )
    }

    private fun findQuickestPath(
        valley: Valley,
        blizzardCache: BlizzardCache,
        minutesPassed: Int,
        start: Position,
        end: Position,
    ): Int {
        var quickestPath = Int.MAX_VALUE
        var iteration = 0
        val startTime = System.currentTimeMillis()

        val states = LinkedList(listOf(ValleyState(start, minutesPassed)))
        val positionsCache = mutableMapOf<Int, HashSet<Position>>()

        while (states.isNotEmpty()) {
            iteration++
            val state = requireNotNull(states.removeFirst())
            val blizzards = blizzardCache.get(state.minutesPassed + 1)

            // abort early if possible
            if (state.minutesPassed > quickestPath) {
                continue // More time has passed than the quickest path
            }
            if (state.minutesPassed + valley.minTimeToEnd(state) > quickestPath) {
                continue // Best case slower than known best
            }
            val choppedMinutes = state.minutesPassed % blizzardCache.size
            if (positionsCache.getOrPut(choppedMinutes, ::HashSet).contains(state.position)) {
                continue // We've been here in a different cycle
            }

            positionsCache.computeIfPresent(choppedMinutes) { _, positions -> positions.also { it.add(state.position) } }

            // determine next moves
            val possibleMoves = (state.position.neighbours)
                .filterNot { it == start }
                .plus(state.position)
                .filterNot { it.hitsWall(valley) }
                .filterNot { it.hits(blizzards) }

            if (possibleMoves.contains(end)) {
                if (state.minutesPassed + 1 < quickestPath) {
                    quickestPath = state.minutesPassed + 1
                    val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
                    println("Reached end in ${quickestPath}min, iteration $iteration, $duration")
                }
                continue
            }

            // filter invalid moves
            for (possibleMove in possibleMoves) {
                val nextState = ValleyState(
                    position = possibleMove,
                    minutesPassed = state.minutesPassed + 1,
                )
                states.add(nextState)
            }
        }
        val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
        println("Finished after $iteration iterations, $duration")
        return quickestPath
    }

    private fun Position.hits(blizzards: List<Blizzard>) = blizzards.map { it.position }.contains(this)
    private fun Position.hitsWall(valley: Valley) =
        y < 0 || y > valley.end.y || valley.walls.map { it.position }.contains(this)

    private data class ValleyState(
        val position: Position,
        val minutesPassed: Int,
    )

    private data class Valley(
        val walls: List<Wall>,
        val start: Position,
        val end: Position,
    ) {
        val xMax: Int get() = end.x
        val yMax: Int get() = end.y - 1

        fun minTimeToEnd(state: ValleyState): Int {
            return end.x - state.position.x + end.y - state.position.y
        }
    }

    private data class Wall(
        val position: Position,
    )

    private fun List<Blizzard>.move(valley: Valley) = map { it.move(valley) }

    private data class BlizzardCache(
        val cache: Map<Int, List<Blizzard>>
    ) {
        val size: Int
            get() = cache.size

        fun get(minute: Int): List<Blizzard> = cache.getValue(minute % cache.size)

        override fun toString(): String {
            return "BlizzardCache[size=$size]"
        }
    }

    private data class Blizzard(
        val position: Position,
        val direction: Direction,
    ) {
        fun move(valley: Valley): Blizzard {
            val newPosition = position.move(direction, valley)
            return Blizzard(newPosition, direction)
        }

        private fun Position.move(direction: Direction, valley: Valley): Position = when (direction) {
            Direction.UP -> {
                val newY = if (y - 1 < 1) valley.yMax else y - 1
                copy(y = newY)
            }

            Direction.DOWN -> {
                val newY = if (y + 1 > valley.yMax) 1 else y + 1
                copy(y = newY)
            }

            Direction.LEFT -> {
                val newX = if (x - 1 < 1) valley.xMax else x - 1
                copy(x = newX)
            }

            Direction.RIGHT -> {
                val newX = if (x + 1 > valley.xMax) 1 else x + 1
                copy(x = newX)
            }
        }
    }

    private data class Position(
        val x: Int,
        val y: Int,
    ) {
        val neighbours: List<Position>
            get() = listOf(
                copy(y = y - 1),
                copy(y = y + 1),
                copy(x = x - 1),
                copy(x = x + 1),
            )
    }

    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        fun char(): Char = when (this) {
            UP -> '^'
            DOWN -> 'v'
            LEFT -> '<'
            RIGHT -> '>'
        }
    }

    private fun parse(input: List<String>): Pair<Valley, BlizzardCache> {
        var start: Position? = null
        var end: Position? = null
        val walls = mutableListOf<Wall>()
        val blizzards = mutableListOf<Blizzard>()
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                val position = Position(x, y)
                when (char) {
                    '.' -> when (y) {
                        0 -> start = position
                        input.size - 1 -> end = position
                    }

                    '#' -> walls.add(Wall(position))
                    '^' -> blizzards.add(Blizzard(position, Direction.UP))
                    'v' -> blizzards.add(Blizzard(position, Direction.DOWN))
                    '<' -> blizzards.add(Blizzard(position, Direction.LEFT))
                    '>' -> blizzards.add(Blizzard(position, Direction.RIGHT))
                }
            }
        }

        val s = requireNotNull(start)
        val e = requireNotNull(end)

        val valley = Valley(walls, s, e)

        val width = e.x - s.x + 1
        val height = e.y - s.y - 1

        val blizzardsRepeatAfter = lcm(height, width)

        val blizzardCache = (1 until blizzardsRepeatAfter)
            .fold(mutableMapOf(0 to blizzards.toList())) { acc, minute ->
                val movedBlizzards = acc.getValue(minute - 1).move(valley)
                acc.apply { put(minute, movedBlizzards) }
            }

        return valley to BlizzardCache(blizzardCache)
    }

    private fun print(valley: Valley, blizzardCache: BlizzardCache, state: ValleyState) {
        println("Minutes passed: ${state.minutesPassed}")
        val blizzards = blizzardCache.get(state.minutesPassed)
        val blizzardsByPosition = blizzards.groupBy { it.position }
        for (y in 0..valley.yMax + 1) {
            for (x in 0..valley.xMax + 1) {
                val position = Position(x, y)
                if (valley.walls.contains(Wall(position))) print('#')
                else if (state.position == position) print('E')
                else {
                    val blizzardsOnPosition = blizzardsByPosition[position].orEmpty()
                    when (blizzards.size) {
                        0 -> print('.')
                        1 -> print(blizzardsOnPosition.single().direction.char())
                        else -> print(blizzardsOnPosition.count())
                    }
                }
            }
            println()
        }
        println()
    }
}
