import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day03 : AbstractDay() {
    @Test
    fun tests() {
        assertEquals(157, compute1(testInput))
        assertEquals(7917, compute1(puzzleInput))

        assertEquals(70, compute2(testInput))
        assertEquals(2585, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .map { it.findDuplicate() }
            .sumOf { it.priority() }
    }

    private fun compute2(input: List<String>): Int {
        return input
            .windowed(size = 3, step = 3) {
                it.findBadge()
            }
            .sumOf { it.priority() }
    }

    private fun List<String>.findBadge(): Char {
        val (e1, e2, e3) = this.map { it.toCharArray().toSet() }
        return e1.intersect(e2).intersect(e3).single()
    }

    private fun String.findDuplicate(): Char {
        val middleIdx = this.length / 2
        val comp1 = this.toCharArray(startIndex = 0, endIndex = middleIdx).toSet()
        val comp2 = this.toCharArray(startIndex = middleIdx).toSet()

        return comp1.intersect(comp2).single()
    }

    private fun Char.priority(): Int {
        return if (isLowerCase()) {
            code - 96
        } else {
            code - 64 + 26
        }
    }
}
