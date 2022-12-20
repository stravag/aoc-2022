import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day19 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(0, compute1(testInput))
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
        val results = input
            .map { parse(it) }
            .map { blueprint ->
                blueprint.number to 0
            }

        TODO()
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private fun parse(line: String): Blueprint {
        val (namePart, rulesPart) = line.split(":")

        val number = namePart.split(" ").last().toInt()
        val rulesParts = rulesPart
            .split(".")
            .map { it.split(" ").mapNotNull { num -> num.toIntOrNull() } }

        return Blueprint(
            number = number,
            oreRobotRequires = mapOf(
                Ore to rulesParts[0][0]
            ),
            clayRobotRequires = mapOf(
                Ore to rulesParts[1][0]
            ),
            obsidianRobotRequires = mapOf(
                Ore to rulesParts[2][0],
                Clay to rulesParts[2][1],
            ),
            geodeRobotRequires = mapOf(
                Ore to rulesParts[3][0],
                Obsidian to rulesParts[3][1],
            )
        )
    }

    class Blueprint(
        val number: Int,
        val oreRobotRequires: Map<Material, Int>,
        val clayRobotRequires: Map<Material, Int>,
        val obsidianRobotRequires: Map<Material, Int>,
        val geodeRobotRequires: Map<Material, Int>,
    )

    sealed interface Robot<T : Material> {
        fun harvest(stash: Stash)
    }

    class Stash(
        private val materials: MutableMap<Material, Int> = mutableMapOf()
    ) {
        init {
            materials[Ore] = 0
            materials[Clay] = 0
            materials[Obsidian] = 0
            materials[Geode] = 0
        }

        fun add(material: Material) {
            materials.computeIfPresent(material) { _, count -> count + 1 }
        }

        fun <T : Material> has(required: Int, material: T): Boolean {
            return materials.getValue(material) >= required
        }

        fun <T : Material> tryRemove(required: Int, material: T): List<T> {
            return if (materials.getValue(material) >= required) {
                materials.computeIfPresent(material) { _, count -> count - required }
                List(required) { material }
            } else {
                emptyList()
            }
        }
    }

    sealed interface Material
    object Ore : Material
    object Clay : Material
    object Obsidian : Material
    object Geode : Material
}

typealias OreRobot = () -> Day19.Ore
typealias ClayRobot = () -> Day19.Clay
typealias ObsidianRobot = () -> Day19.Obsidian
typealias GeodeRobot = () -> Day19.Geode
