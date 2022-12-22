import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

class Day22 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(6032, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(20494, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    @Test
    fun movementParsingTests() {
        val movements = Movements("1234R54321LRL123")
        val actions = mutableListOf<Action>()
        while (movements.hasNext()) {
            actions.add(movements.next())
        }
        assertEquals(
            listOf(
                Move(1234),
                TurnRight,
                Move(54321),
                TurnLeft,
                TurnRight,
                TurnLeft,
                Move(123),
            ),
            actions
        )
    }

    @Test
    fun turnFaceTest() {
        assertEquals(Facing.UP.turn(TurnRight), Facing.RIGHT)
        assertEquals(Facing.UP.turn(TurnLeft), Facing.LEFT)

        assertEquals(Facing.DOWN.turn(TurnRight), Facing.LEFT)
        assertEquals(Facing.DOWN.turn(TurnLeft), Facing.RIGHT)

        assertEquals(Facing.RIGHT.turn(TurnRight), Facing.DOWN)
        assertEquals(Facing.RIGHT.turn(TurnLeft), Facing.UP)

        assertEquals(Facing.LEFT.turn(TurnRight), Facing.UP)
        assertEquals(Facing.LEFT.turn(TurnLeft), Facing.DOWN)
    }

    @Test
    fun moveHorizontal() {
        fun plan(): Plan = parsePlan(listOf(" ... "))
        fun planR(): Plan = plan().apply { facing = Facing.RIGHT }
        fun planL(): Plan = plan().apply { facing = Facing.LEFT }
        fun Plan.m(i: Int): Tile = this.also { it.move(Move(i)) }.currentPosition

        // right
        assertEquals(Tile(0, 1), planR().m(0))
        assertEquals(Tile(0, 2), planR().m(1))
        assertEquals(Tile(0, 1), planR().m(99))

        // left
        assertEquals(Tile(0, 1), planL().m(0))
        assertEquals(Tile(0, 3), planL().m(1))
        assertEquals(Tile(0, 1), planL().m(99))
    }

    @Test
    fun moveHorizontalWall() {
        fun plan(): Plan = parsePlan(listOf(" ..# "))
        fun planR(): Plan = plan().apply { facing = Facing.RIGHT }
        fun planL(): Plan = plan().apply { facing = Facing.LEFT }
        fun Plan.m(i: Int): Tile = this.also { it.move(Move(i)) }.currentPosition

        // right
        assertEquals(Tile(0, 1), planR().m(0))
        assertEquals(Tile(0, 2), planR().m(1))
        assertEquals(Tile(0, 2), planR().m(200))

        // left
        assertEquals(Tile(0, 1), planL().m(0))
        assertEquals(Tile(0, 1), planL().m(1))
        assertEquals(Tile(0, 1), planL().m(200))
    }

    @Test
    fun moveVertical() {
        fun plan() = parsePlan(listOf(" ", ".", ".", " "))
        fun planD(): Plan = plan().apply { facing = Facing.DOWN }
        fun planU(): Plan = plan().apply { facing = Facing.UP }
        fun Plan.m(i: Int): Tile = this.also { it.move(Move(i)) }.currentPosition

        // down
        assertEquals(Tile(1, 0), planD().m(0))
        assertEquals(Tile(2, 0), planD().m(1))
        assertEquals(Tile(1, 0), planD().m(2))

        // up
        assertEquals(Tile(1, 0), planU().m(0))
        assertEquals(Tile(2, 0), planU().m(1))
        assertEquals(Tile(1, 0), planU().m(2))
    }

    private fun compute1(input: List<String>): Int {
        val (plan, movements) = parse(input)

        while (movements.hasNext()) {
            val action = movements.next()
            plan.perform(action)
        }

        val finalPosition = plan.currentPosition
        return listOf(
            1000 * (finalPosition.rowZeroBased + 1),
            4 * (finalPosition.colZeroBased + 1),
            plan.facing.points
        ).sum()
    }


    private fun compute2(input: List<String>): Long {
        TODO()
    }

    sealed interface Element {
        val rowZeroBased: Int
        val colZeroBased: Int
    }

    data class Wall(override val rowZeroBased: Int, override val colZeroBased: Int) : Element
    data class Tile(override val rowZeroBased: Int, override val colZeroBased: Int) : Element

    enum class Facing(val points: Int) {
        RIGHT(0),
        DOWN(1),
        LEFT(2),
        UP(3);

        fun turn(turn: Turn): Facing {
            return when (this) {
                RIGHT -> if (turn is TurnLeft) UP else DOWN
                DOWN -> if (turn is TurnLeft) RIGHT else LEFT
                LEFT -> if (turn is TurnLeft) DOWN else UP
                UP -> if (turn is TurnLeft) LEFT else RIGHT
            }
        }
    }

    data class Plan(
        val elementsByRow: Map<Int, List<Element>>,
        val elementsByCol: Map<Int, List<Element>>,
        var currentPosition: Tile,
        var facing: Facing = Facing.RIGHT
    ) {
        fun perform(action: Action) {
            when (action) {
                is Move -> move(action)
                TurnLeft -> facing = facing.turn(TurnLeft)
                TurnRight -> facing = facing.turn(TurnRight)
            }
        }

        fun move(move: Move) {
            when (facing) {
                Facing.RIGHT -> {
                    val row = elementsByRow.getValue(currentPosition.rowZeroBased)
                    moveIncreasing(move, row)
                }
                Facing.LEFT -> {
                    val row = elementsByRow.getValue(currentPosition.rowZeroBased)
                    moveDecreasing(move, row)
                }
                Facing.DOWN -> {
                    val col = elementsByCol.getValue(currentPosition.colZeroBased)
                    moveIncreasing(move, col)
                }
                Facing.UP -> {
                    val col = elementsByCol.getValue(currentPosition.colZeroBased)
                    moveDecreasing(move, col)
                }
            }
        }

        private fun moveIncreasing(move: Move, elementsInScope: List<Element>) {
            val currentPositionInScope = elementsInScope.indexOf(currentPosition)
            repeat(move.count) {
                val nextPositionInScope = (currentPositionInScope + it + 1) % elementsInScope.size
                val nextElement = elementsInScope[nextPositionInScope]
                if (nextElement is Tile) {
                    currentPosition = nextElement
                } else {
                    return // we hit a wall
                }
            }
        }

        private fun moveDecreasing(move: Move, elementsInScope: List<Element>) {
            val currentPositionInScope = elementsInScope.indexOf(currentPosition)
            repeat(move.count) {
                val nextPositionInScopeRaw = (currentPositionInScope - it - 1) % elementsInScope.size
                val nextPositionInScope = if (nextPositionInScopeRaw < 0) {
                    elementsInScope.size - abs(nextPositionInScopeRaw)
                } else {
                    nextPositionInScopeRaw
                }
                val nextElement = elementsInScope[nextPositionInScope]
                if (nextElement is Tile) {
                    currentPosition = nextElement
                } else {
                    return // we hit a wall
                }
            }
        }

        companion object {
            fun build(elements: List<Element>, startPosition: Tile): Plan {
                val elementsByRow = elements.groupBy { it.rowZeroBased }
                val elementsByCol = elements.groupBy { it.colZeroBased }
                return Plan(
                    elementsByRow = elementsByRow,
                    elementsByCol = elementsByCol,
                    currentPosition = startPosition
                )
            }
        }
    }

    class Movements(
        private val movementsString: String,
        private var position: Int = 0,
    ) {
        fun hasNext(): Boolean = position < movementsString.length

        fun next(): Action {
            val moveCountString = movementsString.drop(position).takeWhile { it.isDigit() }
            position += moveCountString.length

            return if (moveCountString.isNotEmpty()) {
                Move(moveCountString.toInt())
            } else {
                when (movementsString.drop(position++).take(1)) {
                    "R" -> TurnRight
                    "L" -> TurnLeft
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    sealed interface Action
    data class Move(val count: Int) : Action
    sealed interface Turn
    object TurnRight : Action, Turn
    object TurnLeft : Action, Turn

    fun parse(input: List<String>): Pair<Plan, Movements> {
        val plan = parsePlan(input.takeWhile { it.isNotEmpty() })
        val movements = parseMovements(input.last())
        return plan to movements
    }

    private fun parsePlan(planInput: List<String>): Plan {
        val elements = planInput
            .flatMapIndexed { rowZeroBased: Int, planRowInput: String ->
                planRowInput.mapIndexedNotNull { colZeroBased, elementChar ->
                    when (elementChar) {
                        '.' -> Tile(rowZeroBased, colZeroBased)
                        '#' -> Wall(rowZeroBased, colZeroBased)
                        else -> null
                    }
                }
            }
        val startPosition = elements.first { it is Tile } as Tile
        return Plan.build(elements, startPosition)
    }

    private fun parseMovements(movementsString: String): Movements {
        return Movements(movementsString)
    }

}
