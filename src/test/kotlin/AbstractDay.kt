import java.io.File
import kotlin.math.abs
import kotlin.test.assertEquals

abstract class AbstractDay {

    val puzzleInput get() = File("src/test/resources/${this::class.java.simpleName}.txt").readLines()
    val testInput get() = File("src/test/resources/${this::class.java.simpleName}_test.txt").readLines()
}
