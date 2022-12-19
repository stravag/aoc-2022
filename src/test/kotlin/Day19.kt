import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class Day19 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(24, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val results = input.map { Blueprint.parse(it) }.map { blueprint ->
            blueprint.number to blueprint.harvestGeodes(24)
        }

        return results.maxBy { it.second }.let { (number, geodes) -> number * geodes }
    }


    private fun compute2(input: List<String>): Int {
        return input.size
    }

    private class Blueprint(
        val number: Int,
        private val requiredResourcesForAction: Map<Action, Resources>,
    ) {
        fun harvestGeodes(depth: Int): Int {
            println("Starting $this")
            val startTime = System.currentTimeMillis()
            val maxGeodeCount = determineMaxGeodeCount(
                startTime = startTime,
                state = State(
                    robots = Robots(oreRobots = 1),
                    resources = Resources(),
                    remainingMinutes = depth,
                ),
                knownMax = 0,
                iterations = 0,
            )

            val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
            println("Blueprint $number has max geode of $maxGeodeCount for depth=$depth in $duration")
            return maxGeodeCount
        }

        private fun determineMaxGeodeCount(
            startTime: Long,
            state: State,
            knownMax: Int,
            iterations: Int,
        ): Int {
            val blueprint = this
            var max = state.resources.geode

            if (state.maxPossibleGeodeCount <= knownMax) {
                //return knownMax
            }
            if (state.remainingMinutes == 0) {
                return max
            }

            val nextPossibleActions = state.nextPossibleActions(blueprint)

            nextPossibleActions.forEach { nextPossibleAction ->
                val stateIncreased = state.nextStep()
                val stateAfterBuild = stateIncreased.perform(nextPossibleAction, blueprint)
                val optionResult = determineMaxGeodeCount(
                    startTime = startTime,
                    state = stateAfterBuild,
                    knownMax = max,
                    iterations = iterations + 1,
                )
                if (optionResult > max) {
                    val duration = Duration.ofMillis(System.currentTimeMillis() - startTime)
                    println("$max - iteration $iterations, $duration")
                    max = optionResult
                }
            }

            return max
        }

        fun requiredResources(action: Action): Resources {
            return requiredResourcesForAction.getValue(action)
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
                        Action.NOOP to Resources(),
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
        fun perform(action: Action, blueprint: Blueprint): State {
            val requiredResources = blueprint.requiredResources(action)
            val reducedResources = resources - requiredResources
            return State(
                robots = robots.build(action), resources = reducedResources, remainingMinutes = remainingMinutes
            )
        }

        fun nextStep(): State {
            return State(
                robots = robots,
                resources = resources + robots,
                remainingMinutes = remainingMinutes - 1,
            )
        }

        fun nextPossibleActions(blueprint: Blueprint): List<Action> {
            val actions = mutableListOf<Action>()
            if (can(Action.BUILD_GEODE_ROBOT, blueprint)) {
                return listOf(Action.BUILD_GEODE_ROBOT)
            }
            if (can(Action.BUILD_OBSIDIAN_ROBOT, blueprint)) {
                actions.add(Action.BUILD_OBSIDIAN_ROBOT)
            }
            if (can(Action.BUILD_CLAY_ROBOT, blueprint)) {
                actions.add(Action.BUILD_CLAY_ROBOT)
            }
            if (can(Action.BUILD_ORE_ROBOT, blueprint)) {
                actions.add(Action.BUILD_ORE_ROBOT)
            }
            actions.add(Action.NOOP)

            return actions
        }

        val maxPossibleGeodeCount: Int
            get() {
                val geodesFromExistingRobots = robots.geodeRobots * remainingMinutes
                val geodesFromFutureGeodeRobots = (1..remainingMinutes).sum()
                return geodesFromExistingRobots + geodesFromFutureGeodeRobots
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
