fun main() {
    execute(
        day = "Day07",
        part1 = 95437 to ::compute1,
        part2 = 0 to ::compute2,
    )
}

private fun compute1(input: List<String>): Int {

    val folders = mutableListOf<FolderInfo>()
    var pointer = -1
    input.forEach { line ->
        when {
            line == "$ ls" -> Unit
            line.startsWith("dir") -> Unit
            line.startsWith("$ cd") -> {
                when (val dirName = line.split(" ")[2]) {
                    ".." -> {
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

    val currentFolder = folders[pointer]
    val parentFolder = folders[pointer - 1]
    folders[pointer - 1] = parentFolder.increaseSize(currentFolder)
    pointer--

    return folders
        .map { it.size }
        .filter { it <= 100000 }
        .sum()
}

data class FolderInfo(val name: String, val size: Int) {
    fun increaseSize(added: Int) = copy(size = size + added)
    fun increaseSize(folder: FolderInfo) = copy(size = size + folder.size)
}

private fun compute2(input: List<String>): Int {
    return input.size
}
