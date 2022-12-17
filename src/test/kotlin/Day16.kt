import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day16 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(1651, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1653, compute1(puzzleInput))
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
        val valves = parse(input)
        val shortestPaths: Map<Valve, Map<Valve, Int>> = valves.values.associateWith {
            findShortestPaths(it)
        }

        val maxFlow = findHighestFlow(
            openValves = emptySet(),
            shortestPaths = shortestPaths,
            currentValve = valves.getValue("AA"),
            remainingMinutes = 30,
        )

        return maxFlow
    }

    private fun findHighestFlow(
        openValves: Set<Valve>,
        shortestPaths: Map<Valve, Map<Valve, Int>>,
        currentValve: Valve,
        remainingMinutes: Int
    ): Int {
        if (remainingMinutes <= 0) {
            return 0
        }

        var maxFlow = 0
        val nextValves = shortestPaths
            .getValue(currentValve)
            .filterNot { currentValve == it.key }
            .filterNot { openValves.contains(it.key) }

        nextValves.forEach { (nextValve, distance) ->
            val timeUntilValveIsOn = distance + 1
            val flow = findHighestFlow(
                openValves = openValves + currentValve,
                shortestPaths = shortestPaths,
                currentValve = nextValve,
                remainingMinutes = remainingMinutes - timeUntilValveIsOn,
            )
            if (flow > maxFlow) {
                maxFlow = flow
            }
        }

        return remainingMinutes * currentValve.flow + maxFlow
    }

    private fun findShortestPaths(startValve: Valve): Map<Valve, Int> {
        val shortestDistances = mutableMapOf(startValve to 0)
        val unsettled = mutableSetOf(startValve)
        val settled = mutableSetOf<Valve>()
        while (unsettled.isNotEmpty()) {
            val currentValve = unsettled.minBy { shortestDistances[it] ?: Int.MAX_VALUE }
            unsettled.remove(currentValve)
            currentValve.nextValves.forEach { nextValve ->
                if (!settled.contains(nextValve)) {
                    val distanceToSource = shortestDistances.getValue(currentValve)
                    val nextDistance = shortestDistances[nextValve] ?: Int.MAX_VALUE
                    if (distanceToSource + 1 < nextDistance) {
                        shortestDistances.computeIfAbsent(nextValve) { _ -> distanceToSource + 1 }
                    }
                    unsettled.add(nextValve)
                }
            }
            settled.add(currentValve)
        }
        return shortestDistances
            .filter { (valve, _) -> valve.hasFlow() }
    }

    private fun compute2(input: List<String>): Int {
        return parse(input).hashCode()
    }

    private fun parse(input: List<String>): Map<String, Valve> {
        val nodesLookup = mutableMapOf<String, Valve>()
        val regex = "^Valve ([A-Z][A-Z]) has flow rate=([0-9]+); tunnels? leads? to valves? (.*)$".toRegex()
        input.forEach { line ->
            val matches = regex.find(line) ?: throw IllegalArgumentException("Unexpected input: $line")
            val name = matches.groupValues[1].trim()
            val valve = nodesLookup.getOrPut(name, Valve(name))
            val flow = matches.groupValues[2].toInt()
            val otherValves = matches.groupValues[3]
                .split(",")
                .map { it.trim() }
                .map { otherName ->
                    nodesLookup.getOrPut(otherName, Valve(otherName))
                }
            valve.flow = flow
            valve.add(otherValves)
        }
        return nodesLookup
            .filter { it.value.hasFlow() || it.key == "AA" }
    }

    data class Valve(
        val name: String
    ) {
        var flow: Int = 0
        val nextValves: MutableSet<Valve> = mutableSetOf()

        fun add(other: List<Valve>) = nextValves.addAll(other)
        fun hasFlow() = flow != 0

        override fun toString(): String {
            return "$name ($flow)"
        }
    }

    private fun <K, V> MutableMap<K, V>.getOrPut(key: K, ifMissing: V): V {
        val v = get(key)
        return if (v == null) {
            put(key, ifMissing)
            ifMissing
        } else {
            v
        }
    }
}
