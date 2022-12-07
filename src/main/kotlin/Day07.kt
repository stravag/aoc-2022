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
        .map { it.size }
        .filter { it <= 100000 }
        .sum()
}

private fun compute2(input: List<String>): Int {
    val folders = parse(input)
    val unusedSpace = 70000000 - folders.first().size
    val requiredSpace = 30000000 - unusedSpace

    return folders
        .map { it.size }
        .filter { it >= requiredSpace }
        .min()
}

private fun parse(input: List<String>): List<FolderInfo> {
    val folders = mutableListOf<FolderInfo>()
    var pointer = -1
    input.forEach { line ->
        when {
            line == "$ ls" -> Unit
            line.startsWith("dir") -> Unit
            line.startsWith("$ cd") -> {
                when (val dirName = line.split(" ")[2]) {
                    ".." -> {
                        // propagate size to parent
                        val currentFolder = folders[pointer]
                        val parentFolder = folders[pointer - 1]
                        folders[pointer - 1] = parentFolder.increaseSize(currentFolder)
                        pointer--
                    }

                    else -> folders.add(++pointer, FolderInfo(dirName, 0))
                }
            }

            else -> {
                val size = line.split(" ").first().toInt()
                val currentFolder = folders[pointer]
                folders[pointer] = currentFolder.increaseSize(size)
            }
        }
    }

    // propagate size to root
    val currentFolder = folders[pointer]
    val parentFolder = folders[pointer - 1]
    folders[pointer - 1] = parentFolder.increaseSize(currentFolder)
    return folders
}

private data class FolderInfo(val name: String, val size: Int) {
    fun increaseSize(added: Int) = copy(size = size + added)
    fun increaseSize(folder: FolderInfo) = copy(size = size + folder.size)
}
