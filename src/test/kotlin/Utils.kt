import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

fun <T> List<T>.sublistOrNull(fromIndex: Int, toIndex: Int) = try {
    this.subList(fromIndex, toIndex)
} catch (e: Exception) {
    null
}

fun <T> List<T>.sublistOrEmpty(fromIndex: Int, toIndex: Int) = this.sublistOrNull(fromIndex, toIndex) ?: emptyList()

fun sign(i: Int): Int = sign(i.toDouble()).toInt()

fun lcm(number1: Int, number2: Int): Int {
    if (number1 == 0 || number2 == 0) {
        return 0
    }
    val absNumber1 = abs(number1)
    val absNumber2 = abs(number2)
    val absHigherNumber = max(absNumber1, absNumber2)
    val absLowerNumber = min(absNumber1, absNumber2)
    var lcm = absHigherNumber
    while (lcm % absLowerNumber != 0) {
        lcm += absHigherNumber
    }
    return lcm
}
