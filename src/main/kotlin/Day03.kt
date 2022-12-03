object Day03 {
    fun compute(input: List<String>): Int {
        return input
            .map { it.findDuplicate() }
            .sumOf { it.priority() }
    }

    private fun String.findDuplicate(): Char {
        val middleIdx = this.length / 2
        val comp1 = this.toCharArray(startIndex = 0, endIndex = middleIdx)
        val comp2 = this.toCharArray(startIndex = middleIdx)

        return comp1.intersect(comp2.toTypedArray().toSet()).single()
    }

    private fun Char.priority(): Int {
        return if (isLowerCase()) {
            code - 96
        } else {
            code - 64 + 26
        }
    }
}
