import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

object Day17 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(3068, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(3127, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(1, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val wind = parse(input)
        val chamber = Chamber(wind)

        repeat(2022) {
            chamber.dropRock()
        }
        chamber.print()
        return chamber.height
    }

    private fun compute2(input: List<String>): Long {
        val wind = parse(input)
        Chamber(wind)
        TODO("find out after how many iterations it repeats itself. then simply calculate based on that number")
    }

    private fun parse(input: List<String>): Wind {
        return Wind(input.single())
    }

    private class Chamber(
        val wind: Wind,
    ) {
        val rockPoints: MutableSet<P> = mutableSetOf()
        var rockCount = 0
        var height = 0

        fun dropRock() {
            val dropPos = height + 4
            val rock = when (rockCount % 5) {
                0 -> Rock.minus(dropPos)
                1 -> Rock.plus(dropPos)
                2 -> Rock.angle(dropPos)
                3 -> Rock.line(dropPos)
                4 -> Rock.block(dropPos)
                else -> throw RuntimeException()
            }

            do {
                wind.push(rock, rockPoints)
            } while (rock.tryFall(rockPoints))

            markSettled(rock)
        }

        private fun markSettled(rock: Rock) {
            rockCount++
            rockPoints.addAll(rock.points)
            height = max(height, rock.points.maxOf { it.y })
        }

        @Suppress("unused")
        fun print(fallingRock: Rock? = null) {
            println()
            println()
            for (y in (0..height + 6).reversed()) {
                for (x in (-1..7)) {
                    val p = P(x, y)
                    val char = when {
                        y == 0 && x == -1 -> '+'
                        y == 0 && x == 7 -> '+'
                        x == -1 || x == 7 -> '|'
                        y == 0 -> '-'
                        fallingRock?.points.orEmpty().contains(p) -> '@'
                        rockPoints.contains(p) -> '#'
                        else -> '.'
                    }
                    print(char)
                }
                println()
            }
        }
    }

    private class Wind(
        private val input: String
    ) {
        private var count: Int = 0

        val push: (Rock, Set<P>) -> Unit
            get() {
                val direction = input[count++ % input.length]
                return when (direction) {
                    '>' -> Rock::tryPushRight
                    '<' -> Rock::tryPushLeft
                    else -> throw RuntimeException()
                }
            }
    }

    private data class Rock(
        val points: List<P>,
    ) {
        fun tryPushLeft(rockPoints: Set<P>) {
            val nextRockPosition = copy().pushLeft()
            val onLeftWall = nextRockPosition.points.any { it.x < 0 }
            val onOtherRock = checkOnOtherRock(nextRockPosition, rockPoints)
            val canMove = !onLeftWall && !onOtherRock
            if (canMove) {
                points.forEach { it.pushLeft() }
            }
        }

        fun tryPushRight(rockPoints: Set<P>) {
            val nextRockPosition = copy().pushRight()
            val onRightWall = nextRockPosition.points.any { it.x > 6 }
            val onOtherRock = checkOnOtherRock(nextRockPosition, rockPoints)
            val canMove = !onRightWall && !onOtherRock
            if (canMove) {
                points.forEach { it.pushRight() }
            }
        }

        fun tryFall(rockPoints: Set<P>): Boolean {
            val nextRockPosition = copy().fall()
            val onFloor = nextRockPosition.points.any { it.y == 0 }
            val onOtherRock = checkOnOtherRock(nextRockPosition, rockPoints)
            val canMove = !onFloor && !onOtherRock
            return if (canMove) {
                fall()
                true
            } else {
                false
            }
        }

        private fun checkOnOtherRock(rock: Rock, rockPoints: Set<P>) =
            rock.points.any { rockPoints.contains(it) }

        private fun copy(): Rock {
            return Rock(
                points = points.map { it.copy() }
            )
        }

        private fun fall(): Rock {
            points.forEach { it.fall() }
            return this
        }

        private fun pushLeft(): Rock {
            points.forEach { it.pushLeft() }
            return this
        }

        private fun pushRight(): Rock {
            points.forEach { it.pushRight() }
            return this
        }

        companion object {
            fun minus(yPos: Int): Rock {
                val points = (2..5).map { x -> P(x, yPos) }
                return Rock(points)
            }

            fun plus(yPos: Int): Rock {
                return Rock(
                    listOf(
                        P(3, yPos + 2),
                        P(2, yPos + 1), P(3, yPos + 1), P(4, yPos + 1),
                        P(3, yPos),
                    )
                )
            }

            fun angle(yPos: Int): Rock {
                return Rock(
                    listOf(
                        P(4, yPos + 2),
                        P(4, yPos + 1),
                        P(2, yPos), P(3, yPos), P(4, yPos),
                    )
                )
            }

            fun line(yPos: Int): Rock {
                val points = (yPos until yPos + 4).map { y -> P(2, y) }
                return Rock(points)
            }

            fun block(yPos: Int): Rock {
                return Rock(
                    listOf(
                        P(2, yPos + 1), P(3, yPos + 1),
                        P(2, yPos), P(3, yPos),
                    )
                )
            }
        }
    }

    private data class P(
        var x: Int,
        var y: Int,
    ) {
        fun pushLeft() {
            x--
        }

        fun pushRight() {
            x++
        }

        fun fall() {
            y--
        }
    }
}
