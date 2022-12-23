import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class Day19 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(33, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1150, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(56 * 62, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val results = input
            .map { Blueprint.parse(it) }
            .map { blueprint ->
                blueprint.number to maxGeodes(blueprint, 24)
            }

        return results.sumOf { (number, geodes) -> number * geodes }
    }


    private fun compute2(input: List<String>): Int {
        val results = input
            .map { Blueprint.parse(it) }
            .take(3)
            .map { blueprint ->
                maxGeodes(blueprint, 32)
            }

        return results.reduce { acc, i -> acc * i }
    }

    private fun maxGeodes(blueprint: Blueprint, minutes: Int): Int {
        println("Starting $blueprint")

        val startTime = System.currentTimeMillis()

        var max = 0
        var iteration = 0L

        val statesCache = HashSet<State>()
        val states = mutableListOf(
            State(
                robots = Robots(oreRobots = 1),
                resources = Resources(),
                remainingMinutes = minutes,
            )
        )

        while (states.isNotEmpty()) {
            iteration++
            val state = states.removeLast()

            if (state.robots.clayRobots == 0 || state.robots.obsidianRobots == 0) {
                val minutesUntilObsidianRobot = blueprint.minutesUntilProduced(Action.BUILD_OBSIDIAN_ROBOT, state)
                val minutesUntilGeodeRobot = blueprint.minutesUntilProduced(Action.BUILD_GEODE_ROBOT, state)
                if (minutesUntilObsidianRobot + minutesUntilGeodeRobot >= state.remainingMinutes) {
                    // abort not enough time left to build any geode robots
                    continue
                }
                if (maxProduction(state.remainingMinutes - minutesUntilObsidianRobot - minutesUntilGeodeRobot) <= max) {
                    // even best case not better
                    continue
                }
            } else if (state.robots.geodeRobots == 0) {
                val minutesUntilGeodeRobot = blueprint.minutesUntilProduced(Action.BUILD_GEODE_ROBOT, state)
                if (maxProduction(state.remainingMinutes - minutesUntilGeodeRobot) <= max) {
                    continue
                }
            }

            if (statesCache.contains(state)) {
                continue
            }
            statesCache.add(state)

            val nextStates = state.nextPossibleStates(blueprint)

            nextStates.forEach { nextState ->
                if (nextState.remainingMinutes == 0) {
                    if (nextState.resources.geode > max) {
                        max = nextState.resources.geode
                        val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
                        println("$max - iteration $iteration, $duration")
                    }
                } else {
                    states.add(nextState)
                }
            }
        }

        val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
        println("Took $iteration iterations, $duration")

        return max
    }

    private fun maxProduction(minutes: Int): Int {
        return (0 until minutes).sum()
    }


    private data class Blueprint(
        val number: Int,
        private val requiredResourcesForAction: Map<Action, Resources>,
    ) {

        fun requiredResources(action: Action): Resources {
            return requiredResourcesForAction[action] ?: Resources()
        }

        fun maxRequiredResources(type: (Resources) -> Int): Int {
            return requiredResourcesForAction.values.maxOfOrNull(type) ?: 0
        }

        fun minutesUntilProduced(action: Action, state: State): Int {
            val required = requiredResources(action)
            return when (action) {
                Action.NOOP -> 0
                Action.BUILD_ORE_ROBOT -> minutesUntilProduced(
                    count = required.ore, startingCount = state.resources.ore, startingRobots = state.robots.oreRobots
                )

                Action.BUILD_CLAY_ROBOT -> minutesUntilProduced(
                    count = required.ore, startingCount = state.resources.ore, startingRobots = state.robots.oreRobots
                )

                // only clay of interest
                Action.BUILD_OBSIDIAN_ROBOT -> minutesUntilProduced(
                    count = required.clay,
                    startingCount = state.resources.clay,
                    startingRobots = state.robots.clayRobots,
                )

                // only obsidian of interest
                Action.BUILD_GEODE_ROBOT -> minutesUntilProduced(
                    required.obsidian, state.resources.obsidian, state.robots.obsidianRobots
                )
            }
        }

        private fun minutesUntilProduced(count: Int, startingCount: Int, startingRobots: Int): Int {
            var minutes = 0
            var robots = startingRobots
            var sum = startingCount
            while (true) {
                sum += robots
                if (sum >= count) {
                    return minutes
                }
                robots++
                minutes++
            }
        }

        companion object {
            fun parse(line: String): Blueprint {
                val (namePart, rulesPart) = line.split(":")

                val number = namePart.split(" ").last().toInt()
                val rulesParts = rulesPart.split(".").map { it.split(" ").mapNotNull { num -> num.toIntOrNull() } }

                val oreRobotRequires = Resources(
                    ore = rulesParts[0][0]
                )
                val clayRobotRequires = Resources(
                    ore = rulesParts[1][0]
                )
                val obsidianRobotRequires = Resources(
                    ore = rulesParts[2][0],
                    clay = rulesParts[2][1],
                )
                val geodeRobotRequires = Resources(
                    ore = rulesParts[3][0],
                    obsidian = rulesParts[3][1],
                )

                return Blueprint(
                    number = number, requiredResourcesForAction = mapOf(
                        Action.BUILD_ORE_ROBOT to oreRobotRequires,
                        Action.BUILD_CLAY_ROBOT to clayRobotRequires,
                        Action.BUILD_OBSIDIAN_ROBOT to obsidianRobotRequires,
                        Action.BUILD_GEODE_ROBOT to geodeRobotRequires,
                    )
                )
            }
        }
    }

    private enum class Action {
        NOOP, BUILD_ORE_ROBOT, BUILD_CLAY_ROBOT, BUILD_OBSIDIAN_ROBOT, BUILD_GEODE_ROBOT,
    }

    private data class State(
        val robots: Robots,
        val resources: Resources,
        val remainingMinutes: Int,
    ) {
        fun next(action: Action, blueprint: Blueprint): State {
            val requiredResources = blueprint.requiredResources(action)
            val resourcesAfterActionAndGathering = resources - requiredResources + robots
            return State(
                robots = robots.build(action),
                resources = resourcesAfterActionAndGathering,
                remainingMinutes = remainingMinutes - 1
            )
        }

        fun nextPossibleStates(blueprint: Blueprint): List<State> {
            val nextPossibleActions = nextPossibleActions(blueprint)
            return nextPossibleActions.map { action -> next(action, blueprint) }
        }

        private fun nextPossibleActions(blueprint: Blueprint): List<Action> {
            if (can(Action.BUILD_GEODE_ROBOT, blueprint)) {
                return listOf(Action.BUILD_GEODE_ROBOT)
            }

            val actions = ArrayList<Action>(4)
            if (can(Action.BUILD_OBSIDIAN_ROBOT, blueprint)) {
                val maxObsidianConsumption = blueprint.maxRequiredResources { it.obsidian } * remainingMinutes
                val obsidianProductionCapacity = resources.obsidian + (robots.obsidianRobots * remainingMinutes)
                if (maxObsidianConsumption > obsidianProductionCapacity) {
                    actions.add(Action.BUILD_OBSIDIAN_ROBOT)
                }
            }
            if (can(Action.BUILD_CLAY_ROBOT, blueprint)) {
                val maxClayConsumption = blueprint.maxRequiredResources { it.clay } * remainingMinutes
                val clayProductionCapacity = resources.clay + (robots.clayRobots * remainingMinutes)
                if (maxClayConsumption > clayProductionCapacity) {
                    actions.add(Action.BUILD_CLAY_ROBOT)
                }
            }
            if (can(Action.BUILD_ORE_ROBOT, blueprint)) {
                val maxOreConsumption = blueprint.maxRequiredResources { it.ore } * remainingMinutes
                val oreProductionCapacity = resources.ore + (robots.oreRobots * remainingMinutes)
                if (maxOreConsumption > oreProductionCapacity) {
                    actions.add(Action.BUILD_ORE_ROBOT)
                }
            }

            actions.add(Action.NOOP);

            return actions
        }

        private fun can(action: Action, blueprint: Blueprint): Boolean {
            val requiredResources = blueprint.requiredResources(action)
            return resources.ore >= requiredResources.ore && resources.clay >= requiredResources.clay && resources.obsidian >= requiredResources.obsidian && resources.geode >= requiredResources.geode
        }
    }

    private data class Resources(
        val ore: Int = 0,
        val clay: Int = 0,
        val obsidian: Int = 0,
        val geode: Int = 0,
    ) {

        operator fun minus(other: Resources): Resources {
            return Resources(
                ore = ore - other.ore,
                clay = clay - other.clay,
                obsidian = obsidian - other.obsidian,
                geode = geode - other.geode,
            )
        }

        operator fun plus(other: Resources): Resources {
            return Resources(
                ore = ore + other.ore,
                clay = clay + other.clay,
                obsidian = obsidian + other.obsidian,
                geode = geode + other.geode,
            )
        }

        operator fun plus(other: Robots): Resources {
            return Resources(
                ore = ore + other.oreRobots,
                clay = clay + other.clayRobots,
                obsidian = obsidian + other.obsidianRobots,
                geode = geode + other.geodeRobots,
            )
        }
    }

    private data class Robots(
        val oreRobots: Int = 0,
        val clayRobots: Int = 0,
        val obsidianRobots: Int = 0,
        val geodeRobots: Int = 0,
    ) {
        fun build(action: Action): Robots {
            return when (action) {
                Action.NOOP -> copy()
                Action.BUILD_ORE_ROBOT -> copy(oreRobots = oreRobots + 1)
                Action.BUILD_CLAY_ROBOT -> copy(clayRobots = clayRobots + 1)
                Action.BUILD_OBSIDIAN_ROBOT -> copy(obsidianRobots = obsidianRobots + 1)
                Action.BUILD_GEODE_ROBOT -> copy(geodeRobots = geodeRobots + 1)
            }
        }
    }
}
