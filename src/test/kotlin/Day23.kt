import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day23 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(110, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(4091, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(20, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1036, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val elves = parse(input)

        repeat(10) {
            elves.round()
        }
        elves.print()

        return elves.result1
    }


    private fun compute2(input: List<String>): Int {
        val elves = parse(input)

        do {
            val moved = elves.round()
        } while (moved)

        return elves.result2
    }

    class Elves(
        private var currentPositions: LinkedHashSet<Elf>,
        private var roundCount: Int = 0,
    ) {
        constructor(positions: List<Elf>) : this(LinkedHashSet(positions))

        fun round(): Boolean {
            val moveSuggestions = moveSuggestions()
                .sortedBy { it.current }

            val groupedSuggestions = moveSuggestions
                .groupBy { it.suggested }

            val newPositions = groupedSuggestions
                .flatMap { (_, elvesWithSameSuggestion) ->
                    if (elvesWithSameSuggestion.size > 1) {
                        elvesWithSameSuggestion.map { it.current } // don't move
                    } else {
                        elvesWithSameSuggestion.map { it.suggested } // move to suggestion
                    }
                }

            require(newPositions.size == currentPositions.size)
            val hasMoved = !currentPositions.containsAll(newPositions)
            currentPositions = LinkedHashSet(newPositions)
            roundCount++
            return hasMoved
        }

        val result1: Int
            get() {
                val minX = currentPositions.minOf { it.x }
                val maxX = currentPositions.maxOf { it.x }
                val minY = currentPositions.minOf { it.y }
                val maxY = currentPositions.maxOf { it.y }
                val width = maxX - minX + 1
                val height = maxY - minY + 1
                return width * height - currentPositions.size
            }

        val result2: Int
            get() = roundCount

        fun print() {
            val minX = currentPositions.minOf { it.x }
            val maxX = currentPositions.maxOf { it.x }
            val minY = currentPositions.minOf { it.y }
            val maxY = currentPositions.maxOf { it.y }
            for (y in minY - 1..maxY + 1) {
                for (x in minX - 1..maxX + 1) {
                    if (currentPositions.contains(Elf(x, y))) print('#') else print('.')
                }
                println()
            }
        }

        private fun moveSuggestions(): List<MoveSuggestion> {
            fun List<Elf>.allFree(): Boolean = none { currentPositions.contains(it) }

            fun Elf.suggestion(): MoveSuggestion? {
                val checks: List<Pair<DirectionCheck, MoveSuggester>> = listOf(
                    DirectionCheck { it.n.allFree() } to MoveSuggester { MoveSuggestion(it.north, it) },
                    DirectionCheck { it.s.allFree() } to MoveSuggester { MoveSuggestion(it.south, it) },
                    DirectionCheck { it.w.allFree() } to MoveSuggester { MoveSuggestion(it.west, it) },
                    DirectionCheck { it.e.allFree() } to MoveSuggester { MoveSuggestion(it.east, it) },
                )

                val checkOffset = roundCount % checks.size
                return (checks.indices).map { i ->
                    val checkIdx = (i + checkOffset) % checks.size
                    val (check, suggester) = checks[checkIdx]
                    if (check.check(this)) suggester.suggest(this) else null
                }.firstOrNull { it != null }
            }

            return currentPositions.map { elf ->
                val elfSuggestion = elf.suggestion()
                when {
                    elf.allAdjacent.allFree() -> MoveSuggestion(elf, elf) // stay
                    elfSuggestion != null -> elfSuggestion
                    else -> MoveSuggestion(elf, elf) // stay
                }
            }
        }
    }

    fun interface DirectionCheck {
        fun check(elf: Elf): Boolean
    }

    fun interface MoveSuggester {
        fun suggest(elf: Elf): MoveSuggestion
    }

    data class MoveSuggestion(
        val suggested: Elf,
        val current: Elf,
    )

    data class Elf(
        val x: Int,
        val y: Int,
    ) : Comparable<Elf> {
        val allAdjacent: List<Elf>
            get() = listOf(
                north,
                northEast,
                northWest,
                south,
                southEast,
                southWest,
                east,
                west,
            )

        val n: List<Elf> get() = listOf(north, northEast, northWest)
        val s: List<Elf> get() = listOf(south, southEast, southWest)
        val w: List<Elf> get() = listOf(west, northWest, southWest)
        val e: List<Elf> get() = listOf(east, northEast, southEast)

        val north: Elf get() = Elf(x, y - 1)
        val northEast: Elf get() = Elf(x + 1, y - 1)
        val northWest: Elf get() = Elf(x - 1, y - 1)
        val south: Elf get() = Elf(x, y + 1)
        val southEast: Elf get() = Elf(x + 1, y + 1)
        val southWest: Elf get() = Elf(x - 1, y + 1)
        val east: Elf get() = Elf(x + 1, y)
        val west: Elf get() = Elf(x - 1, y)

        override fun compareTo(other: Elf): Int {
            if (y != other.y) return y.compareTo(other.y)
            return x.compareTo(other.x)
        }
    }

    private fun parse(input: List<String>): Elves {
        val positions = input.flatMapIndexed { y: Int, row: String ->
            row
                .mapIndexedNotNull { x, char ->
                    if (char == '#') Elf(x, y) else null
                }
        }
        return Elves(positions)
    }
}
