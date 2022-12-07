fun main() {
    execute(
        day = "Day07",
        part1 = 95437 to ::compute1,
        part2 = 24933642 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int {
    val folders = parse(input)
    return folders
        .map { it.value }
        .filter { it <= 100000 }
        .sum()
}

private fun compute2(input: List<String>): Int {
    val folders = parse(input)
    val unusedSpace = 70000000 - folders.entries.first().value
    val requiredSpace = 30000000 - unusedSpace

    return folders
        .map { it.value }
        .filter { it >= requiredSpace }
        .min()
}

private fun parse(input: List<String>): Map<String, Int> {
    val folders = linkedMapOf<String, Int>()
    val currentPath = mutableListOf<String>()
    input.forEach { line ->
        when {
            line.isChangeDir() -> {
                when (val dirName = line.split(" ")[2]) {
                    ".." -> currentPath.removeLast()
                    else -> {
                        currentPath.add(dirName)
                        folders[currentPath.joinToString()] = 0
                    }
                }
            }

            line.isFile() -> {
                val fileSize = line.split(" ").first().toInt()
                currentPath
                    .runningFold(emptyList<String>()) { acc, s -> acc + s }
                    .forEach { path ->
                        folders.computeIfPresent(path.joinToString()) { _, folderSize -> folderSize + fileSize }
                    }

            }
        }
    }

    return folders
}

fun String.isChangeDir() = startsWith("$ cd")
fun String.isFile() = matches("^[0-9]+ .*".toRegex())
