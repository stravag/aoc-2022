import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.sqrt
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
        assertEquals(55343, compute2(testInput, 4))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(55343, compute2(puzzleInput, 50))
    }

    @Test
    fun part2MoveTests() {
        fun Cube.verify(
            givenSide: SideName, givenRow: Int, givenCol: Int, givenFacing: Facing,
            side: SideName, row: Int, col: Int, facing: Facing,
        ) {
            this.currentPosition = givenSide to Tile(givenRow, givenCol)
            this.facing = givenFacing
            this.perform(Move(1))
            assertEquals(side to Tile(row, col), this.currentPosition)
            assertEquals(facing, this.facing)

            this.perform(TurnLeft)
            this.perform(TurnLeft)
            this.perform(Move(1))
            assertEquals(givenSide to Tile(givenRow, givenCol), this.currentPosition)
            this.perform(TurnLeft)
            this.perform(TurnLeft)
            assertEquals(givenFacing, this.facing)
        }

        val cube = parseCube(puzzleInput, 50)
        cube.verify(
            givenSide = "01", givenRow = 0, givenCol = 50, givenFacing = Facing.UP,
            side = "30", row = 150, col = 0, facing = Facing.RIGHT,
        )
        cube.verify(
            givenSide = "01", givenRow = 0, givenCol = 50, givenFacing = Facing.LEFT,
            side = "20", row = 149, col = 0, facing = Facing.RIGHT,
        )
        cube.verify(
            givenSide = "01", givenRow = 0, givenCol = 99, givenFacing = Facing.RIGHT,
            side = "02", row = 0, col = 100, facing = Facing.RIGHT,
        )
        cube.verify(
            givenSide = "01", givenRow = 49, givenCol = 99, givenFacing = Facing.DOWN,
            side = "11", row = 50, col = 99, facing = Facing.DOWN,
        )

        cube.verify(
            givenSide = "02", givenRow = 0, givenCol = 149, givenFacing = Facing.UP,
            side = "30", row = 199, col = 49, facing = Facing.UP,
        )
        cube.verify(
            givenSide = "02", givenRow = 0, givenCol = 100, givenFacing = Facing.LEFT,
            side = "01", row = 0, col = 99, facing = Facing.LEFT,
        )
        cube.verify(
            givenSide = "02", givenRow = 0, givenCol = 149, givenFacing = Facing.RIGHT,
            side = "21", row = 149, col = 99, facing = Facing.LEFT,
        )
        cube.verify(
            givenSide = "02", givenRow = 49, givenCol = 149, givenFacing = Facing.DOWN,
            side = "11", row = 99, col = 99, facing = Facing.LEFT,
        )
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
        val plan = parsePlan(input.takeWhile { it.isNotEmpty() })
        val movements = parseMovements(input.last())

        while (movements.hasNext()) {
            val action = movements.next()
            plan.perform(action)
        }

        val finalPosition = plan.currentPosition
        return listOf(
            1000 * (finalPosition.row + 1),
            4 * (finalPosition.col + 1),
            plan.facing.points
        ).sum()
    }


    private fun compute2(input: List<String>, cubeSize: Int): Int {
        val cube = parseCube(input.takeWhile { it.isNotEmpty() }, cubeSize)
        val movements = parseMovements(input.last())

        while (movements.hasNext()) {
            val action = movements.next()
            cube.perform(action)
        }

        val finalPosition = cube.currentPosition.second
        return listOf(
            1000 * (finalPosition.row + 1),
            4 * (finalPosition.col + 1),
            cube.facing.points
        ).sum()
    }

    sealed interface Element {
        val row: Int
        val col: Int
    }

    data class Wall(override val row: Int, override val col: Int) : Element
    data class Tile(override val row: Int, override val col: Int) : Element

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
                    val row = elementsByRow.getValue(currentPosition.row)
                    moveIncreasing(move, row)
                }

                Facing.LEFT -> {
                    val row = elementsByRow.getValue(currentPosition.row)
                    moveDecreasing(move, row)
                }

                Facing.DOWN -> {
                    val col = elementsByCol.getValue(currentPosition.col)
                    moveIncreasing(move, col)
                }

                Facing.UP -> {
                    val col = elementsByCol.getValue(currentPosition.col)
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
                val elementsByRow = elements.groupBy { it.row }
                val elementsByCol = elements.groupBy { it.col }
                return Plan(
                    elementsByRow = elementsByRow,
                    elementsByCol = elementsByCol,
                    currentPosition = startPosition
                )
            }
        }
    }

    data class Cube(
        val lookupsBySide: Map<SideName, ElementLookup>,
        val size: Int,
        var currentPosition: Pair<SideName, Tile>,
        var facing: Facing = Facing.RIGHT
    ) {
        fun perform(action: Action) {
            when (action) {
                is Move -> move(action)
                TurnLeft -> facing = facing.turn(TurnLeft)
                TurnRight -> facing = facing.turn(TurnRight)
            }
        }

        private fun Int.reverse(offset: Int = 0): Int {
            return (0 until size).reversed().elementAt(this % size) + offset
        }

        private fun move(move: Move) {
            try {
                repeat(move.count) {
                    val overflowInfo = when (currentPosition.first) {
                        "01" -> when (facing) {
                            Facing.RIGHT -> overFlow(
                                newSide = "02",
                                newCol = { it.col + 1 },
                            )

                            Facing.DOWN -> overFlow(
                                newSide = "11",
                                newRow = { it.row + 1 }
                            )

                            Facing.LEFT -> overFlow(
                                newSide = "20",
                                newRow = { it.row.reverse(100) },
                                newCol = { 0 },
                                newFace = Facing.RIGHT,
                            )

                            Facing.UP -> overFlow(
                                newSide = "30",
                                newRow = { it.col + 100 },
                                newCol = { 0 },
                                newFace = Facing.RIGHT,
                            )

                        }

                        "02" -> when (facing) {
                            Facing.RIGHT -> overFlow(
                                newSide = "21",
                                newRow = { it.row.reverse(100) },
                                newCol = { 99 },
                                newFace = Facing.LEFT,
                            )

                            Facing.DOWN -> overFlow(
                                newSide = "11",
                                newRow = { it.col - 50 },
                                newCol = { 99 },
                                newFace = Facing.LEFT,
                            )

                            Facing.LEFT -> overFlow(
                                newSide = "01",
                                newCol = { it.col - 1 }
                            )

                            Facing.UP -> overFlow(
                                newSide = "30",
                                newRow = { 199 },
                                newCol = { it.col - 100 },
                            )

                        }

                        "11" -> when (facing) {
                            Facing.RIGHT -> overFlow(
                                newSide = "02",
                                newRow = { 49 },
                                newCol = { it.row + 50 },
                                newFace = Facing.UP,
                            )

                            Facing.DOWN -> overFlow(
                                newSide = "21",
                                newRow = { it.row + 1 }
                            )

                            Facing.LEFT -> overFlow(
                                newSide = "20",
                                newRow = { 100 },
                                newCol = { it.row - 50 },
                                newFace = Facing.DOWN,
                            )

                            Facing.UP -> overFlow(
                                newSide = "01",
                                newRow = { it.row - 1 }
                            )

                        }

                        "20" -> when (facing) {
                            Facing.RIGHT -> overFlow(
                                newSide = "21",
                                newCol = { it.col + 1 },
                            )

                            Facing.DOWN -> overFlow(
                                newSide = "30",
                                newRow = { it.row + 1 }
                            )

                            Facing.LEFT -> overFlow(
                                newSide = "01",
                                newRow = { it.row.reverse() },
                                newCol = { 50 },
                                newFace = Facing.RIGHT,
                            )

                            Facing.UP -> overFlow(
                                newSide = "11",
                                newRow = { it.col + 50 },
                                newCol = { 50 },
                                newFace = Facing.RIGHT,
                            )

                        }

                        "21" -> when (facing) {
                            Facing.RIGHT -> overFlow(
                                newSide = "02",
                                newRow = { it.row.reverse() },
                                newCol = { 149 },
                                newFace = Facing.LEFT,
                            )

                            Facing.DOWN -> overFlow(
                                newSide = "30",
                                newRow = { it.col + 100 },
                                newCol = { 49 },
                                newFace = Facing.LEFT,
                            )

                            Facing.LEFT -> overFlow(
                                newSide = "20",
                                newCol = { it.col - 1 },
                            )

                            Facing.UP -> overFlow(
                                newSide = "11",
                                newRow = { it.row - 1 }
                            )

                        }

                        "30" -> when (facing) {
                            Facing.RIGHT -> overFlow(
                                newSide = "21",
                                newRow = { 149 },
                                newCol = { it.row - 100 },
                                newFace = Facing.UP,
                            )

                            Facing.DOWN -> overFlow(
                                newSide = "02",
                                newRow = { 0 },
                                newCol = { it.col + 100 },
                            )

                            Facing.LEFT -> overFlow(
                                newSide = "01",
                                newRow = { 0 },
                                newCol = { it.row - 100 },
                                newFace = Facing.DOWN,
                            )

                            Facing.UP -> overFlow(
                                newSide = "20",
                                newRow = { it.row - 1 }
                            )

                        }

                        else -> throw IllegalArgumentException("Unexpected sideName")
                    }
                    tryMove(overflowInfo)
                }
            } catch (e: HitWall) {
                // we hit a wall
            }
        }

        private fun tryMove(overflowInfo: OverflowInfo) {
            val (currentByRow, currentByCol) = lookupsBySide.getValue(currentPosition.first)
            when (facing) {
                Facing.RIGHT -> tryMove(
                    scopeLookup = currentByRow,
                    scopePos = { it.row },
                    scopePosDiff = { it + 1 },
                    overflowInfo = overflowInfo
                )

                Facing.DOWN -> tryMove(
                    scopeLookup = currentByCol,
                    scopePos = { it.col },
                    scopePosDiff = { it + 1 },
                    overflowInfo = overflowInfo
                )

                Facing.LEFT -> tryMove(
                    scopeLookup = currentByRow,
                    scopePos = { it.row },
                    scopePosDiff = { it - 1 },
                    overflowInfo = overflowInfo
                )

                Facing.UP -> tryMove(
                    scopeLookup = currentByCol,
                    scopePos = { it.col },
                    scopePosDiff = { it - 1 },
                    overflowInfo = overflowInfo
                )

            }
        }

        private fun tryMove(
            scopeLookup: Map<Int, List<Element>>,
            scopePos: (Element) -> Int,
            scopePosDiff: (Int) -> Int,
            overflowInfo: OverflowInfo
        ) {
            val (currentSide, currentTile) = currentPosition
            val scope = scopeLookup.getValue(scopePos(currentTile))
            val currentPositionInScope = scope.indexOf(currentTile)
            val newPositionInScope = scopePosDiff(currentPositionInScope)
            val (newSide, newElement, newFacing) = if (0 <= newPositionInScope && newPositionInScope < scope.size) {
                Triple(currentSide, scope[newPositionInScope], facing)
            } else {
                getOverflow(currentTile, overflowInfo)
            }
            if (newElement is Tile) {
                currentPosition = Pair(newSide, newElement)
                facing = newFacing
            } else {
                throw HitWall
            }
        }

        private fun getOverflow(tile: Tile, overflowInfo: OverflowInfo): Triple<SideName, Element, Facing> {
            val newRow = overflowInfo.newRow(tile)
            val newCol = overflowInfo.newCol(tile)

            val elementsOfNewFace = lookupsBySide.getValue(overflowInfo.newSide)
            val overFlowElement = elementsOfNewFace.byRow
                .getValue(newRow)
                .single { it.col == newCol }

            return Triple(overflowInfo.newSide, overFlowElement, overflowInfo.newFace)
        }

        private fun overFlow(
            newSide: SideName,
            newRow: (Tile) -> Int = { it.row },
            newCol: (Tile) -> Int = { it.col },
            newFace: Facing = this.facing,
        ) = OverflowInfo(newSide, newRow, newCol, newFace)

        data class OverflowInfo(
            val newSide: SideName,
            val newRow: (Tile) -> Int,
            val newCol: (Tile) -> Int,
            val newFace: Facing,
        )

        object HitWall : RuntimeException()

        companion object {
            fun build(elements: List<Pair<SideName, Element>>, startPosition: Pair<SideName, Tile>): Cube {
                val cubeSize = sqrt(elements.size / 6.0)
                val lookupsBySide = elements
                    .groupBy(
                        keySelector = { it.first },
                        valueTransform = { it.second }
                    )
                    .mapValues { (_, elementsOfSide) ->
                        ElementLookup(
                            byRow = elementsOfSide.groupBy { it.row },
                            byCol = elementsOfSide.groupBy { it.col },
                        )
                    }
                return Cube(lookupsBySide, cubeSize.toInt(), startPosition)
            }
        }

        data class ElementLookup(
            val byRow: Map<Int, List<Element>>,
            val byCol: Map<Int, List<Element>>,
        )
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

    private fun parseCube(planInput: List<String>, cubeSize: Int): Cube {
        val elements = planInput
            .flatMapIndexed { rowZeroBased: Int, planRowInput: String ->
                planRowInput.mapIndexedNotNull { colZeroBased, elementChar ->
                    val element = when (elementChar) {
                        '.' -> Tile(rowZeroBased, colZeroBased)
                        '#' -> Wall(rowZeroBased, colZeroBased)
                        else -> null
                    }
                    element?.let { it.sideName(cubeSize) to it }
                }
            }

        val startPosition = elements
            .first { it.second is Tile }
            .let { it.first to it.second as Tile }

        return Cube.build(elements, startPosition)
    }

    private fun Element.sideName(cubeSize: Int): SideName {
        return "${row / cubeSize}${col / cubeSize}"
    }

    private fun parseMovements(movementsString: String): Movements {
        return Movements(movementsString)
    }
}

typealias SideName = String
