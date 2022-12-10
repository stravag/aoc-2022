import java.io.File

abstract class AbstractDay {

    private val day: String get() = this::class.java.simpleName
    val puzzleInput: List<String> get() = File("src/test/resources/$day/puzzle.txt").readLines()
    val testInput get() = File("src/test/resources/$day/test.txt").readLines()
}
