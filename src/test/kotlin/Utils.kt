import kotlin.math.sign

fun <T> List<T>.sublistOrNull(fromIndex: Int, toIndex: Int) = try {
    this.subList(fromIndex, toIndex)
} catch (e: Exception) {
    null
}

fun <T> List<T>.sublistOrEmpty(fromIndex: Int, toIndex: Int) = this.sublistOrNull(fromIndex, toIndex) ?: emptyList()

fun sign(i: Int): Int = sign(i.toDouble()).toInt()
